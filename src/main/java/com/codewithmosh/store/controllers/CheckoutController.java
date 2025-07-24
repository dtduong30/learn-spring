package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.repositories.CartRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {
  private final CartRepository cartRepository;

  @PostMapping("")
  public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
    var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
    if (cart == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "Cart not found"));
    }

    if (cart.getItems().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("message", "Cart not found"));
    }
    return null;
  }
}
