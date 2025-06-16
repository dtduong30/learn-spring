package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.RegisterUserRequestDto;
import com.codewithmosh.store.dtos.UpdateUserRequestDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUserEntity(RegisterUserRequestDto userRequestDto);
    void update(UpdateUserRequestDto request, @MappingTarget User user);
}
