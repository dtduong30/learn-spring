package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class CheckoutResponse {
  private Long id;

  public CheckoutResponse(Long orderId) {
    this.id = orderId;
  }
}
