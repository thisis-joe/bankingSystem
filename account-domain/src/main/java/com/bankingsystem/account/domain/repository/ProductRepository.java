package com.bankingsystem.account.domain.repository;

import com.bankingsystem.account.domain.model.Product;
import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findByProductCode(String productCode);

    Product save(Product product);
}
