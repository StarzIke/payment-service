package com.assessment.PaymentProcessor.service;

import com.assessment.PaymentProcessor.dto.ParentRequest;
import com.assessment.PaymentProcessor.model.Parent;
import com.assessment.PaymentProcessor.repository.ParentRepository;
import com.assessment.PaymentProcessor.repository.StudentRepository;
import com.assessment.PaymentProcessor.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParentService {
    private final ParentRepository parentRepository;

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ParentService(ParentRepository parentRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.userRepository =  userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
    }

    public Parent createParent(ParentRequest request) {
        // Create parent entity
        Parent parent = new Parent();
        parent.setFullName(request.getFullName());
        parent.setAccountBalance(request.getBalance());
        parent.setStudents(request.getStudents().stream().map(id -> {return studentRepository.findById(id).orElse(null);}).collect(Collectors.toList()));
        Parent savedParent = parentRepository.save(parent);
        log.info("savedParent: "+savedParent.toString());
        return savedParent;
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
    }
}
