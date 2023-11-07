package org.example.endpoint.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.example.service.ProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new product",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Some error occurred",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(description = "errors messages",
                            oneOf = {InternalExceptionDTO.class, StructuredExceptionDTO.class}))}
            )})
    @PostMapping
    public ProductDTO create(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        return productService.save(productCreateDTO);
    }

    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @GetMapping("/{uuid}")
    public ProductDTO getById(@PathVariable UUID uuid) {
        return productService.findByUUID(uuid);
    }

    @Operation(summary = "Get product by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid name supplied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @GetMapping("/name")
    public ProductDTO getByName(@RequestParam(value = "name") String name) {
        return productService.findByName(name);
    }

}
