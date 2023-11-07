package org.example.endpoint.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.example.service.api.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Product")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final IProductService productService;

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

    @Operation(summary = "Get page of products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Products not found",
                    content = @Content)})
    @GetMapping
    public Page<ProductDTO> getPage(@PageableDefault(size = 20, sort = {"name"}) Pageable pageable) {
        return productService.getPage(pageable);
    }

    @Operation(summary = "Update name of the product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name updated"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PatchMapping("/{uuid}/name/{name}")
    public ProductDTO updateName(@PathVariable UUID uuid, @PathVariable String name) {
        return productService.updateName(uuid, name);
    }

    @Operation(summary = "Update rules of the product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rules updated"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PatchMapping("/{uuid}/rules")
    public ProductDTO updateRules(@PathVariable(name = "uuid") UUID uuid, @Valid @RequestBody List<RuleDTO> ruleDTOList) {
        return productService.updateRules(uuid, ruleDTOList);
    }

    @Operation(summary = "Update items of the product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items updated"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PatchMapping("/{uuid}/items")
    public ProductDTO updateItems(@PathVariable UUID uuid, @Valid @RequestBody List<ItemDTO> itemDTOList) {
        return productService.updateItems(uuid, itemDTOList);
    }

    @Operation(summary = "Add new item to the product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to product"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PostMapping("/{uuid}/items/add")
    public ProductDTO addItem(@PathVariable UUID uuid, @Valid @RequestBody ItemDTO itemDTO) {
        return productService.addItem(uuid, itemDTO);
    }

    @Operation(summary = "Increase or decrease quantity of the stored item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity was changed"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PatchMapping("/{uuid}/items/add")
    public ProductDTO addToItemQuantity(@PathVariable UUID uuid,
                                        @RequestParam(name = "expires_at", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
                                        @RequestParam(name = "summand") Integer summand) {
        return productService.addToItemQuantity(uuid, expiresAt, summand);
    }

    @Operation(summary = "Change expiration date of the stored item of the product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expiration date was changed"),
            @ApiResponse(responseCode = "400", description = "Invalid params passed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalExceptionDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    @PatchMapping("/{uuid}/items/expiration")
    public ProductDTO changeItemExpirationDate(@PathVariable UUID uuid,
                                               @RequestParam(name = "expires_at", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
                                               @RequestParam(name = "new_date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate replacement) {
        return productService.changeItemExpirationDate(uuid, expiresAt, replacement);
    }
}
