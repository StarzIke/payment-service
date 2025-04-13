package com.assessment.PaymentProcessor.service;

import com.assessment.PaymentProcessor.dto.PaymentRequest;
import com.assessment.PaymentProcessor.exception.AdminNotFoundException;
import com.assessment.PaymentProcessor.exception.ParentNotFoundException;
import com.assessment.PaymentProcessor.exception.PaymentFailedException;
import com.assessment.PaymentProcessor.exception.StudentNotFoundException;
import com.assessment.PaymentProcessor.model.Parent;
import com.assessment.PaymentProcessor.model.PaymentLog;
import com.assessment.PaymentProcessor.model.Student;
import com.assessment.PaymentProcessor.model.User;
import com.assessment.PaymentProcessor.model.enums.PaymentStatus;
import com.assessment.PaymentProcessor.repository.ParentRepository;
import com.assessment.PaymentProcessor.repository.PaymentRepository;
import com.assessment.PaymentProcessor.repository.StudentRepository;
import com.assessment.PaymentProcessor.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @Transactional
    public PaymentLog processStudentPayment(String adminUsername, PaymentRequest request) {
        User admin = userRepository.findByUsername(adminUsername).orElseThrow(() -> new AdminNotFoundException("Admin not found"));

        if (request == null || request.getPaymentAmount() == null || request.getPaymentAmount() <= 0) {
            throw new IllegalArgumentException("Invalid payment request or amount");
        }

        PaymentLog paymentLog = initializePayment(admin, request);

        try {
            Parent initiatingParent =
                    parentRepository.findById(request.getParentId().get(0)).orElseThrow(() -> new ParentNotFoundException("Parent not found"));
            Student student =
                    studentRepository.findById(request.getStudentId()).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            paymentLog.setInitiatingParent(initiatingParent);
            paymentLog.setStudent(student);

            double adjustedAmount = calculateAdjustedAmount(request.getPaymentAmount(), request.getDynamicRate());

            validateBalance(initiatingParent, adjustedAmount);

            List<Long> affectedParentIds = processSharedStudentPayments(student, adjustedAmount);

            if (affectedParentIds.isEmpty()) {
                deductBalance(initiatingParent, adjustedAmount);
            }

            student.setAccountBalance(student.getAccountBalance() + adjustedAmount);
            studentRepository.save(student);

            paymentLog.setAffectedParentIds(affectedParentIds);
            paymentLog.setStatus(PaymentStatus.SUCCESS);
            return paymentRepository.save(paymentLog);

        } catch (Exception ex) {
            paymentLog.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(paymentLog);
            throw new PaymentFailedException("Payment failed for with the name: " + ex.getMessage());
        }
    }

    private PaymentLog initializePayment(User admin, PaymentRequest request) {
        PaymentLog payment = new PaymentLog();
        payment.setProcessedBy(admin);
        payment.setAmount(request.getPaymentAmount());
        return payment;
    }

    public double calculateAdjustedAmount(Double paymentAmount, Double dynamicRate) {
        return paymentAmount * (1 + dynamicRate);
    }

    private void validateBalance(Parent parent, double amount) {
        if (parent.getAccountBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Parent has $" + parent.getAccountBalance() +
                    " but needs $" + amount);
        }
    }

    private List<Long> processSharedStudentPayments(Student student, double adjustedAmount) {
        List<Long> affectedParentIds = new ArrayList<>();
        if (student.getParents() != null && student.getParents().size() > 1) {
            for (Parent sharedParent : student.getParents()) {
                validateBalance(sharedParent, adjustedAmount / 2);
                deductBalance(sharedParent, adjustedAmount / 2);
                affectedParentIds.add(sharedParent.getId());
            }
        }
        return affectedParentIds;
    }

    private void deductBalance(Parent parent, double amount) {
        parent.setAccountBalance(parent.getAccountBalance() - amount);
        parentRepository.save(parent);
    }

    public List<PaymentLog> getPaymentsByParentId(Long parentId) {
        // Validate that the user exists
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent record not found"));

        // Retrieve payments where the parent was the payer
        return paymentRepository.findByInitiatingParent(parent);
    }

}
