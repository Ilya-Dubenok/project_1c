package org.example.dao;

import org.example.base.BaseRepositoryContainerTest;
import org.example.dao.repositories.ICategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Test Repository Methods")
public class RepositoryTest extends BaseRepositoryContainerTest {


    @Autowired
    private ICategoryRepository categoryRepository;

    @Test
    public void test1() {
        Assertions.assertEquals(0, categoryRepository.count());
    }

}
