package com.assessment.PaymentProcessor.service;

import com.assessment.PaymentProcessor.dto.StudentRequest;
import com.assessment.PaymentProcessor.model.Parent;
import com.assessment.PaymentProcessor.model.Student;
import com.assessment.PaymentProcessor.model.User;
import com.assessment.PaymentProcessor.model.enums.Role;
import com.assessment.PaymentProcessor.repository.ParentRepository;
import com.assessment.PaymentProcessor.repository.StudentRepository;
import com.assessment.PaymentProcessor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student createStudent(StudentRequest request) {

        // Create student entity
        Student student = new Student();
        student.setFullName(request.getName());
        student.setAccountBalance(request.getBalance());

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
