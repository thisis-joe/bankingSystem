package com.bankingsystem.account.infrastructure.persistence.jpa;

import com.bankingsystem.account.domain.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductJpaRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);
}
