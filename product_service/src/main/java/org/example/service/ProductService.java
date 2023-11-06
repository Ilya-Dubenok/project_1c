package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.dao.repository.IProductRepository;
import org.example.service.api.CategoryClient;
import org.example.service.api.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ModelMapper mapper;

    private final IProductRepository productRepository;

    private final CategoryClient categoryClient;

    @Override
    public ProductDTO save(ProductCreateDTO productCreateDTO) {
        return null;
    }

    @Override
    public ProductDTO findByUUID(UUID uuid) {
        return null;
    }

    @Override
    public ProductDTO findByName(String name) {
        return null;
    }

    @Override
    public Page<ProductDTO> getPage(Pageable pageable) {
        return null;
    }

    @Override
    public ProductDTO updateName(UUID productUuid, String name) {
        return null;
    }

    @Override
    public ProductDTO updateRules(UUID productUuid, List<RuleDTO> rules) {
        return null;
    }

    @Override
    public ProductDTO updateItems(UUID productUuid, List<ItemDTO> items) {
        return null;
    }

    @Override
    public ProductDTO changeItem(UUID productUuid, String itemName, ItemDTO replacement) {
        return null;
    }

    @Override
    public ProductDTO addToItemQuantity(UUID productUuid, String itemName, Integer summand) {
        return null;
    }

    @Override
    public ProductDTO changeItemExpirationDate(UUID productUuid, String itemName, LocalDate replacement) {
        return null;
    }

    @Override
    public void delete(UUID productUuid) {

    }
}
