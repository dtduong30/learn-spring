package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.CategoryDto;
import com.codewithmosh.store.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
}
