package com.assessment.PaymentProcessor.service;

import com.assessment.PaymentProcessor.dto.ParentRequest;
import com.assessment.PaymentProcessor.model.Parent;
import com.assessment.PaymentProcessor.repository.ParentRepository;
import com.assessment.PaymentProcessor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParentService {
    private final ParentRepository parentRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ParentService(ParentRepository parentRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.parentRepository = parentRepository;
        this.userRepository =  userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Parent createParent(ParentRequest request) {
        // Create parent entity
        Parent parent = new Parent();
        parent.setFullName(request.getName());
        parent.setAccountBalance(request.getBalance());
        return parentRepository.save(parent);
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
    }
}
