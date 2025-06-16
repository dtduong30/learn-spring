package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    public String name;
    public String email;
}
