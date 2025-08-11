package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class ErrorDto {
	private String message;
	
	public ErrorDto(String message) {
		this.message = message;
	}
}
