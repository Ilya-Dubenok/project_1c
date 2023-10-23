package org.example.dto.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO <T> {

    private Integer number;

    private Integer size;

    private Integer totalPages;

    private Long totalElements;

    private boolean first;

    private Integer numberOfElements;

    private boolean last;

    private List<T> content;


}
