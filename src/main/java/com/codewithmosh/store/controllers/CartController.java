package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequestDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ) {
        try {
            var cart = new Cart();
            System.out.println("cart = " + cart);
            cartRepository.save(cart);
            var cartDto = cartMapper.toDto(cart);
            System.out.println("cart = " + cartDto);
            var uri = uriBuilder.path("/cart/{id}").buildAndExpand(cartDto.getId()).toUri();
            return ResponseEntity.created(uri).body(cartDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request
    ) {
        try {
            var cart = cartRepository.getCartWithItems(cartId).orElse(null);
            if (cart == null) {
                return ResponseEntity.notFound().build();
            }
            var product = productRepository.findById(request.getProductId()).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().build();
            }
            // assign responsibility for cart object
            var cartItem = cart.addItem(product);
            cartRepository.save(cart);
            var cartItemDto = cartMapper.toEntity(cartItem);

            return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(
            @PathVariable UUID cartId
    ) {
        try {
            var cart = cartRepository.getCartWithItems(cartId).orElse(null);
            if (cart == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(cartMapper.toDto(cart));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequestDto request
    ) {
        try {
            var cart = cartRepository.getCartWithItems(cartId).orElse(null);
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Cart not found")
                );
            }
            var cartItem = cart.getItem(productId);

            if (cartItem == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Product was not found in the cart")
                );
            }

            cartItem.setQuantity(request.getQuantity());
            cartRepository.save(cart);

            return ResponseEntity.ok(cartMapper.toEntity(cartItem));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId
    ) {
        try {
            var cart = cartRepository.getCartWithItems(cartId).orElse(null);
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Cart not found")
                );
            }
            cart.removeItem(productId);
            cartRepository.save(cart);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Clear items on cart, not delete a cart
    @DeleteMapping("/{cartId}/items/")
    public ResponseEntity<?> clearCart(
            @PathVariable("cartId") UUID cartId
    ) {
        try {
            var cart = cartRepository.getCartWithItems(cartId).orElse(null);
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            cart.clear();
            cartRepository.save(cart);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
