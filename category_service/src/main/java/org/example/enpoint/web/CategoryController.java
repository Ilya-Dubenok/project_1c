package org.example.enpoint.web;


import lombok.AllArgsConstructor;
import org.example.core.dto.CategoryCreateDTO;
import org.example.core.dto.CategoryDTO;
import org.example.core.dto.CategoryUpdateDTO;
import org.example.dao.entities.Category;
import org.example.dto.page.PageDTO;
import org.example.service.api.ICategoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
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

        if (null != category) {

            CategoryDTO dto = mapper.map(category, CategoryDTO.class);

            return new ResponseEntity<>(dto, HttpStatus.OK);

        } else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }


    }

    @GetMapping
    public ResponseEntity<PageDTO<CategoryDTO>> getPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "20") Integer size) {

        Page<Category> categoryPage = categoryService.getPage(page, size);

        Type pageDTOType = new TypeToken<PageDTO<CategoryDTO>>() {
        }.getType();

        PageDTO<CategoryDTO> res = mapper.map(categoryPage, pageDTOType);

        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @PutMapping(value = "/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody CategoryUpdateDTO categoryUpdateDTO) {

        categoryService.updateNameAndRules(uuid, categoryUpdateDTO);

        return new ResponseEntity<>(HttpStatus.OK);

    }


    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {

        categoryService.delete(uuid);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
