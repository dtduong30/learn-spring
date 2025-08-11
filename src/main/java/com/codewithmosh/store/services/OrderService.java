package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.OrderDto;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
	private final AuthService authService;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	
	public List<OrderDto> getAllOrders() {
		var customer = authService.getCurrentUser();
		var orders = orderRepository.findAllByCustomer(customer);
		return orders.stream().map(orderMapper::toDto).toList();
	}
	
	public OrderDto getOrder(Long orderId) {
		var order = orderRepository
				.getOrderWithItems(orderId)
				.orElseThrow(OrderNotFoundException::new);
		var customer = authService.getCurrentUser();
		if (!order.isPlacedBy(customer)) {
			throw new AccessDeniedException("You dont have access this order");
		}
		return orderMapper.toDto(order);
	}
}
