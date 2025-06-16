package com.codewithmosh.store.dtos;

import lombok.Data;

@Data // Getter, setter, toString ...
public class RegisterUserRequestDto {
    private String name;
    private String password;
    private String email;
}
