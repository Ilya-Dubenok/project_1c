package org.example.dao.repositories;

import org.example.dao.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ICategoryRepository extends JpaRepository<Category, UUID> {

    Category findByName(String name);

    List<Category> findByParent_IdEquals(UUID parentId);

}
