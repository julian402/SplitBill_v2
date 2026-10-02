package ue.edu.co.splitbill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

// Gasto de un grupo: lo pago un integrante y se reparte en partes iguales entre todos
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_id")
    private Long id;

    @Column(name = "exp_group_id")
    private Long groupId;

    // Integrante (Member) que pago
    @Column(name = "exp_payer_id")
    private Long payerId;

    @Column(name = "exp_description")
    private String description;

    // Pesos enteros
    @Column(name = "exp_amount")
    private Long amount;

    @Column(name = "exp_date")
    private LocalDate date = LocalDate.now();

    @Column(name = "exp_status")
    private Integer status = 1;

    // Constructor vacio, JPA lo necesita
    public Expense() {
    }

    // Para registrar un gasto nuevo
    public Expense(Long groupId, Long payerId, String description, Long amount) {
        this.groupId = groupId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
