package org.example.enpoint.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.example.dao.entities.Category;
import org.example.service.api.ICategoryService;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Category")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    private final ICategoryService categoryService;

    private final ModelMapper mapper;


    @Operation(summary = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new category",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Some error occurred",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(description = "errors messages",
                            oneOf = {InternalExceptionDTO.class, StructuredExceptionDTO.class}))}
            )})
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        categoryService.save(categoryCreateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)})
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

    @Operation(summary = "Get page of categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)})
    @GetMapping
    public Page<CategoryDTO> getPage(@PageableDefault(size = 20, sort = {"name"}) Pageable pageable) {
        return categoryService.getPage(pageable);
    }

    @Operation(summary = "update name and/or rules for categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Some error occurred",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(description = "errors messages",
                            oneOf = {InternalExceptionDTO.class, StructuredExceptionDTO.class}))}
            )})
    @PutMapping(value = "/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO) {

        categoryService.updateNameAndRules(uuid, categoryUpdateDTO);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Some error occurred",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(description = "errors messages",
                            oneOf = {InternalExceptionDTO.class, StructuredExceptionDTO.class}))}
            )})
    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        categoryService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
