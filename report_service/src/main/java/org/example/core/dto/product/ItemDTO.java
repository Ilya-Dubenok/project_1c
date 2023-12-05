package org.example.core.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private Integer quantity;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiresAt;

}
