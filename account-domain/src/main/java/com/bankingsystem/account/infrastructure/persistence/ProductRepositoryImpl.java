package com.bankingsystem.account.infrastructure.persistence;

import com.bankingsystem.account.domain.model.Product;
import com.bankingsystem.account.domain.repository.ProductRepository;
import com.bankingsystem.account.infrastructure.persistence.jpa.SpringDataProductJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final SpringDataProductJpaRepository springDataProductJpaRepository;

    public ProductRepositoryImpl(SpringDataProductJpaRepository springDataProductJpaRepository) {
        this.springDataProductJpaRepository = springDataProductJpaRepository;
    }

    @Override
    public Optional<Product> findByProductCode(String productCode) {
        return springDataProductJpaRepository.findByProductCode(productCode);
    }

    @Override
    public Product save(Product product) {
        return springDataProductJpaRepository.save(product);
    }
}
