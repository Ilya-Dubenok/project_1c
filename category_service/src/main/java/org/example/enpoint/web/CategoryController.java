package org.example.enpoint.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.core.dto.CategoryCreateDTO;
import org.example.core.dto.CategoryDTO;
import org.example.core.dto.CategoryUpdateDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.example.dao.entities.Category;
import org.example.core.dto.PageDTO;
import org.example.service.api.ICategoryService;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.UUID;

@Tag(name = "Category")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    private ICategoryService categoryService;

    private ModelMapper mapper;


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
    public ResponseEntity<?> create(@RequestBody CategoryCreateDTO categoryCreateDTO) {
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
    public ResponseEntity<PageDTO<CategoryDTO>> getPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "20") Integer size) {

        Page<Category> categoryPage = categoryService.getPage(page, size);

        Type pageDTOType = new TypeToken<PageDTO<CategoryDTO>>() {}.getType();

        PageDTO<CategoryDTO> res = mapper.map(categoryPage, pageDTOType);

        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @Operation(summary = "update name and/or rules for categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Some error occurred",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(description = "errors messages",
                            oneOf = {InternalExceptionDTO.class, StructuredExceptionDTO.class}))}
            )})
    @PutMapping(value = "/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody CategoryUpdateDTO categoryUpdateDTO) {

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
