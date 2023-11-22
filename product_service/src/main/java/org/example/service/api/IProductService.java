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

    ProductDTO findByUUID(UUID uuid);

    ProductDTO findByName(String name);

    Page<ProductDTO> getPage(Pageable pageable);

    ProductDTO updateName(UUID productUuid, String name);

    ProductDTO updateRules(UUID productUuid, List<RuleDTO> ruleDTOList);

    ProductDTO updateItems(UUID productUuid, List<ItemDTO> itemDTOList);

    ProductDTO addItem(UUID productUuid, ItemDTO itemDTO);

    ProductDTO addToItemQuantity(UUID productUuid, LocalDate expiresAt, Integer summand);

    ProductDTO changeItemExpirationDate(UUID productUuid, LocalDate expiresAt, LocalDate replacement);

    void delete(UUID productUuid);
}
