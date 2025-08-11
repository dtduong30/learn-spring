package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
	@Id
	@GeneratedValue()
	@Column(name = "id", columnDefinition = "BINARY(16)")
	@JdbcTypeCode(SqlTypes.BINARY)
	private UUID id;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<CartItem> items = new LinkedHashSet<>();
	
	public BigDecimal getTotalPrice() {
		return items.stream()
				.map(CartItem::getTotalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public CartItem getItem(Long productId) {
		return items.stream()
				.filter(item -> item.getProduct().getId().equals(productId))
				.findFirst()
				.orElse(null);
	}
	
	public CartItem addItem(Product product) {
		var cartItem = getItem(product.getId());
		if (cartItem != null) {
			cartItem.setQuantity(cartItem.getQuantity() + 1);
		} else {
			cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setQuantity(1);
			cartItem.setCart(this);
			items.add(cartItem);
		}
		return cartItem;
	}
	
	public void removeItem(Long productId) {
		var cartItem = getItem(productId);
		if (cartItem != null) {
			items.remove(cartItem);
			cartItem.setCart(null);
		}
	}
	
	public void clear() {
		items.clear();
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
}