package org.example.dao.repository;

import org.example.dao.entities.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends MongoRepository<Product, UUID> {

    Optional<Product> findByName(String name);

}
