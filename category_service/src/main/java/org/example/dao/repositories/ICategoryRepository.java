package org.example.dao.repositories;

import org.example.dao.entities.Category;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface ICategoryRepository extends ListCrudRepository<Category, UUID> {


    List<Category> findByParent_UuidEquals(UUID uuid);

}
