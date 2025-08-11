package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.services.CheckoutService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {
	private final CheckoutService checkoutService;
	
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("")
	public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
		return checkoutService.checkout(request);
	}
	
	@ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
	public ResponseEntity<ErrorDto> handleCartException(RuntimeException e) {
		return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
	}
}
