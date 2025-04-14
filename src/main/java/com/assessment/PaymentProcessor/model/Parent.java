package com.assessment.PaymentProcessor.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "parent")
public class Parent extends BaseEntity{
    private String fullName;

    private Double accountBalance;

    @ManyToMany(mappedBy = "parents", cascade = CascadeType.ALL)
    private List<Student> students;

}
