package com.assessment.PaymentProcessor.comtroller;


import com.assessment.PaymentProcessor.dto.PaymentRequest;
import com.assessment.PaymentProcessor.model.PaymentLog;
import com.assessment.PaymentProcessor.model.User;
import com.assessment.PaymentProcessor.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentProcessorController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/processing")
    @PreAuthorize("hasRole('ADMIN')") // Ensures only admins process payments
    public ResponseEntity<String> initiatePayment(@RequestParam String adminUsername, @RequestBody PaymentRequest request) {

        paymentService.processStudentPayment(adminUsername, request);
        return ResponseEntity.ok("Payment successfully processed by Admin: " + adminUsername);
    }

    @GetMapping("/history")
        @PreAuthorize("hasRole('PARENT')")
        public ResponseEntity<List<PaymentLog>> getUserPayments(@RequestParam Long parentId) {
            return ResponseEntity.ok(paymentService.getPaymentsByParentId(parentId));
    }
}
