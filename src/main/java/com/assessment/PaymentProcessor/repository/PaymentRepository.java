package com.assessment.PaymentProcessor.repository;

import com.assessment.PaymentProcessor.model.Parent;
import com.assessment.PaymentProcessor.model.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentLog, Long> {
    List<PaymentLog> findByInitiatingParent(Parent initiatingParent);

}
