package org.example.enpoint.web;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.core.dto.CategoryCreateDTO;
import org.example.core.dto.CategoryDTO;
import org.example.dao.entities.Category;
import org.example.service.api.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    private ICategoryService categoryService;

    private ModelMapper mapper;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryCreateDTO categoryCreateDTO) {
        categoryService.save(categoryCreateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable UUID uuid) {
        Category category = categoryService.findByUUID(uuid);

        CategoryDTO dto = mapper.map(category, CategoryDTO.class);

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

}
