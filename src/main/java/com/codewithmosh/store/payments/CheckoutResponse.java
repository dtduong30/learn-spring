package com.codewithmosh.store.payments;

import lombok.Data;

@Data
public class CheckoutResponse {
	private Long id;
	private String checkoutUrl;
	
	public CheckoutResponse(Long orderId, String checkoutUrl) {
		this.id = orderId;
		this.checkoutUrl = checkoutUrl;
	}
}
