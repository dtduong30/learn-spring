package com.codewithmosh.store.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDto {
    @JsonProperty("userId")
    private Long id;

    private String name;
    private String email;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdAt;
}
