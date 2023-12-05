package org.example.service.api;

import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IProductService {

    ProductDTO save(ProductCreateDTO productCreateDTO);

    ProductDTO findById(UUID id);

    ProductDTO findByName(String name);

    List<ProductDTO> findAll();

    Page<ProductDTO> getPage(Pageable pageable);

    ProductDTO updateName(UUID productId, String name);

    ProductDTO updateRules(UUID productId, List<RuleDTO> ruleDTOList);

    ProductDTO updateItems(UUID productId, List<ItemDTO> itemDTOList);

    ProductDTO addItem(UUID productId, ItemDTO itemDTO);

    ProductDTO addToItemQuantity(UUID productId, LocalDate expiresAt, Integer summand);

    ProductDTO changeItemExpirationDate(UUID productId, LocalDate expiresAt, LocalDate replacement);

    void delete(UUID productId);
}
