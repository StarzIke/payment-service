package com.assessment.PaymentProcessor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "student")
public class Student extends BaseEntity {

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "balance")
    private Double accountBalance;

    @ManyToMany
    @JoinTable(name = "student_parent",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id"))
    private List<Parent> parents;

}
