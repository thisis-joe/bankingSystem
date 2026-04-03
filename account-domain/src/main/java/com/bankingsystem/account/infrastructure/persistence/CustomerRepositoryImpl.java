package com.bankingsystem.account.infrastructure.persistence;

import com.bankingsystem.account.domain.model.Customer;
import com.bankingsystem.account.domain.repository.CustomerRepository;
import com.bankingsystem.account.infrastructure.persistence.jpa.SpringDataCustomerJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final SpringDataCustomerJpaRepository springDataCustomerJpaRepository;

    public CustomerRepositoryImpl(SpringDataCustomerJpaRepository springDataCustomerJpaRepository) {
        this.springDataCustomerJpaRepository = springDataCustomerJpaRepository;
    }

    @Override
    public Optional<Customer> findByCustomerNo(String customerNo) {
        return springDataCustomerJpaRepository.findByCustomerNo(customerNo);
    }

    @Override
    public Customer save(Customer customer) {
        return springDataCustomerJpaRepository.save(customer);
    }
}
