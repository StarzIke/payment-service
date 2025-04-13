package com.assessment.PaymentProcessor.repository;


import com.assessment.PaymentProcessor.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

}