package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.services.CheckoutService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {
	private final CheckoutService checkoutService;
	
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("")
	public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
		try {
			return ResponseEntity.ok(checkoutService.checkout(request));
		} catch (StripeException e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorDto("Error creating a checkout session"));
		}
	}
	
	@ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
	public ResponseEntity<ErrorDto> handleCartException(RuntimeException e) {
		return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
	}
}
