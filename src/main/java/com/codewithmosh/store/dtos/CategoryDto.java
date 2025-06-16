package com.codewithmosh.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryDto {
    @JsonProperty("categoryId")
    private Byte id;
    @JsonProperty("categoryName")
    private String name;
}
