package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class ProductDto {
    private long id;
    private String name;
    private BigDecimal price;
    private String description;
    private CategoryDto category;
}
