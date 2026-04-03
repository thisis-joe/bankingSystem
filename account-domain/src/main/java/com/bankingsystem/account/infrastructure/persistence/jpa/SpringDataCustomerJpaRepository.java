package com.bankingsystem.account.infrastructure.persistence.jpa;

import com.bankingsystem.account.domain.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerJpaRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerNo(String customerNo);
}
