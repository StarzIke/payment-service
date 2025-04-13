package com.assessment.PaymentProcessor.dto;

import com.assessment.PaymentProcessor.model.Student;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ParentRequest {
     private String name;
     private Double balance;
     List<Student> students;

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public Double getBalance() {
          return balance;
     }

     public void setBalance(Double balance) {
          this.balance = balance;
     }

     public List<Student> getStudents() {
          return students;
     }

     public void setStudents(List<Student> students) {
          this.students = students;
     }
}
