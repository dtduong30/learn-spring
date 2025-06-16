package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.dtos.ProductRequestDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    ProductDto toProductDto(Product product);
    Product toEntity(ProductRequestDto productDto);
    void update(ProductRequestDto dto, @MappingTarget Product product);
}
