package org.example.service;

import org.example.base.BaseRepositoryContainerTest;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.exception.EntityNotFoundException;
import org.example.dao.repositories.ICategoryRepository;
import org.example.service.api.ICategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@SpringBootTest
@DisplayName("Test CategoryService Methods")
public class CategoryServiceTest extends BaseRepositoryContainerTest {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICategoryService categoryService;

    @AfterEach
    public void deleteAllData() {
        categoryRepository.deleteAll();
    }

    @Test
    public void saveWorks() {
        categoryService.save(new CategoryCreateDTO("test_name1", null, null));
    }

    @Test
    public void findByIdWorks() {
        System.out.println(BaseRepositoryContainerTest.postgres.isRunning());
        CategoryDTO testCategory = categoryService.save(new CategoryCreateDTO("test_name1", null, null));
        CategoryDTO extractedByFindMethodCategory = categoryService.findById(testCategory.getId());
        Assertions.assertEquals(testCategory, extractedByFindMethodCategory);
    }

    @Test
    public void findByWrongIdThrows() {
        categoryService.save(new CategoryCreateDTO("test_name1", null, null));
        Assertions.assertThrows(EntityNotFoundException.class, () -> categoryService.findById(UUID.randomUUID()));
    }

}
