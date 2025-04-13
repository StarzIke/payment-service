package com.assessment.PaymentProcessor.model;

import com.assessment.PaymentProcessor.model.enums.PaymentStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "payment")
public class PaymentLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User processedBy;  // The Admin who processed the payment

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "initiating_parent_id", nullable = false)
    private Parent initiatingParent; // The parent who made the payment

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private PaymentStatus status;

    @ElementCollection
    private List<Long> affectedParentIds;



}
