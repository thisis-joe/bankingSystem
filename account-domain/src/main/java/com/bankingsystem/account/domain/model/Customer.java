package com.bankingsystem.account.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_no", nullable = false, length = 30, unique = true)
    private String customerNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CustomerStatus status;

    protected Customer() {
    }

    public Customer(String customerNo, CustomerType customerType, String name, CustomerStatus status) {
        this.customerNo = customerNo;
        this.customerType = customerType;
        this.name = name;
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public String getName() {
        return name;
    }

    public CustomerStatus getStatus() {
        return status;
    }
}
