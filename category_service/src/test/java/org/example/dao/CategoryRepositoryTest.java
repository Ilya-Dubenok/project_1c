package org.example.dao;

import org.example.base.BaseRepositoryContainerTest;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.example.dao.entities.Category;
import org.example.dao.repositories.ICategoryRepository;
import org.example.utils.exception.DataBaseExceptionParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@DisplayName("Test Repository Methods")
public class CategoryRepositoryTest extends BaseRepositoryContainerTest {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private DataBaseExceptionParser dataBaseExceptionParser;

    @AfterEach
    public void deleteAllData() {
        categoryRepository.deleteAll();
    }

    @Test
    public void saveMethodWorks() {
        categoryRepository.save(new Category(UUID.randomUUID(), "test_name1"));
    }

    @Test
    public void saveMethodWithNoNameThrows() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(new Category(UUID.randomUUID(), null)));
    }

    @Test
    public void saveMethodWithExistingNameThrows() {
        String sameName = "test_name1";
        categoryRepository.save(new Category(UUID.randomUUID(), sameName));
        DataIntegrityViolationException exc = Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(new Category(UUID.randomUUID(), sameName)));
        Assertions.assertTrue(dataBaseExceptionParser.fillIfExceptionRecognized(exc, new InternalExceptionDTO()));
    }

    @Test
    public void findByName() {
        String testName1 = "test_name1";
        String testName2 = "test_Name2";
        categoryRepository.save(new Category(UUID.randomUUID(), testName1));
        categoryRepository.save(new Category(UUID.randomUUID(), testName2));
        Category category = categoryRepository.findByName(testName1);
        Assertions.assertEquals(testName1, category.getName());
    }

    @Test
    public void findBy_ParentId() {
        UUID parentUuid1 = UUID.randomUUID();
        UUID parentUuid2 = UUID.randomUUID();
        Category parent1 = new Category(parentUuid1, "parent1");
        Category parent2 = new Category(parentUuid2, "parent2");
        categoryRepository.saveAll(List.of(parent1, parent2));

        Category child1OfParent1 = new Category(UUID.randomUUID(), "child1OfParent1", parent1, null);
        Category child2OfParent1 = new Category(UUID.randomUUID(), "child2OfParent1", parent1, null);
        Category child1OfParent2 = new Category(UUID.randomUUID(), "child1OfParent2", parent2, null);
        categoryRepository.saveAll(List.of(child1OfParent1, child2OfParent1, child1OfParent2));

        List<Category> listOfChildrenOfParent1 = categoryRepository.findByParent_IdEquals(parentUuid1);
        Assertions.assertEquals(2, listOfChildrenOfParent1.size());
        Assertions.assertEquals(2, listOfChildrenOfParent1.stream().filter(category -> !Objects.equals("child1OfParent2", category.getName())).count());

        List<Category> listOfChildrenOfParent2 = categoryRepository.findByParent_IdEquals(parentUuid2);
        Assertions.assertEquals(1, listOfChildrenOfParent2.size());
        Assertions.assertEquals("child1OfParent2", listOfChildrenOfParent2.get(0).getName());
    }
}
