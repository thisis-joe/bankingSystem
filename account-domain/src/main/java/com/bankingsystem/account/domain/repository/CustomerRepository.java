package com.bankingsystem.account.domain.repository;

import com.bankingsystem.account.domain.model.Customer;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findByCustomerNo(String customerNo);

    Customer save(Customer customer);
}
