package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.services.CheckoutService;
import com.codewithmosh.store.services.WebhookRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {
	private final CheckoutService checkoutService;
	
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("")
	public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
		return checkoutService.checkout(request);
	}
	
	@PostMapping("/webhook")
	public void handleWebhook(
			@RequestHeader Map<String, String> headers,
			@RequestBody String payload
	) {
		checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
	}
	
	@ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
	public ResponseEntity<ErrorDto> handleCartException(RuntimeException e) {
		return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
	}
	
	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<?> handlePaymentException() {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorDto("Error creating a checkout session"));
	}
}
