package org.example.repository;

import org.example.base.BaseRepositoryContainerTest;
import org.example.dao.entities.product.Product;
import org.example.dao.repository.IProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@DisplayName("Testing product repository")
public class ProductRepositoryTest extends BaseRepositoryContainerTest {

    @Autowired
    private IProductRepository repository;

    @AfterEach
    public void deleteAllData() {
        repository.deleteAll();
    }

    @Test
    public void findByName() {
        String initName = "some name";
        repository.save(new Product(UUID.randomUUID(), UUID.randomUUID(), initName, null, null));
        Optional<Product> optionalProduct = repository.findByName("some name");
        Assertions.assertEquals(initName, optionalProduct.orElseThrow().getName());
    }

    @Test
    public void saveForExistingNameThrows() {
        String sharedName = "some name";
        repository.save(new Product(UUID.randomUUID(), UUID.randomUUID(), sharedName, null, null));
        Assertions.assertThrows(DuplicateKeyException.class, () -> repository.save(new Product(UUID.randomUUID(), UUID.randomUUID(), sharedName, null, null)));
    }
}
