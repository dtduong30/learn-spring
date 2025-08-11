package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.dtos.OrderDto;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
	private final OrderService orderService;
	
	@GetMapping("")
	public List<OrderDto> getAllOrders() {
		return orderService.getAllOrders();
	}
	
	@GetMapping("/{orderId}")
	public OrderDto getOrder(
			@PathVariable Long orderId
	) {
		return orderService.getOrder(orderId);
	}
	
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorDto> handleOrderNotFoundException(Exception ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(ex.getMessage()));
	}
}
