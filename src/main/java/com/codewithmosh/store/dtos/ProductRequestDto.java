package com.codewithmosh.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDto {
    private String name;
    private BigDecimal price;
    private String description;

    @JsonProperty("categoryId")
    private Byte categoryId;
}
