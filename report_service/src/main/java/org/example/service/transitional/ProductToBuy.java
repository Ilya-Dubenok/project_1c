package org.example.service.transitional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.product.ProductDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductToBuy {

    private ProductDTO productDTO;

    private Integer quantity = 0;

    private List<CategoryDTO> categories;

    public ProductToBuy(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public void addQuantity(Integer quantityToBuy) {
        this.quantity = quantity + quantityToBuy;
    }
}
