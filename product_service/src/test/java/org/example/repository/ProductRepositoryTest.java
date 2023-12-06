package org.example.repository;

import org.example.base.BaseRepositoryContainerTest;
import org.example.dao.repository.IProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Testing product repository")
public class ProductRepositoryTest extends BaseRepositoryContainerTest {

    @Autowired
    private IProductRepository repository;

    @Test
    public void test1() {

    }


}
