package org.example.core.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    private Integer quantity;

    private LocalDate expiresAt;

}
