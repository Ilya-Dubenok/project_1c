package org.example.core.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.validation.NotLaterThanToday;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    private Integer quantity;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotLaterThanToday
    private LocalDate expiresAt;

}
