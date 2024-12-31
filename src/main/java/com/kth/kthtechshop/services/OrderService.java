package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.order.CreateOrderDTO;
import com.kth.kthtechshop.dto.order.GetOrdersDTO;
import com.kth.kthtechshop.dto.order.OrderDTO;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.exception.ForbiddenException;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.repository.OrderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDTO getOrder(Long orderId) {
        return null;
    }

    public ListResponse<OrderDTO> getOrders(GetOrdersDTO params) {
        return null;
    }

    @Transactional
    public ResponseEntity<?> createOrder(Long userId, @Valid CreateOrderDTO newOrder) {
        return ResponseEntity.status(201).body(String.format("order with id is %d is created", 1));
    }

    public ResponseEntity<?> cancelOrder(boolean isAdmin, long userId, Long orderId) {
        var orderCont = this.orderRepository.findById(orderId);
        if (orderCont.isEmpty()) throw new NotFoundException();
        var order = orderCont.get();
        if (order.getId() != userId && !isAdmin) throw new ForbiddenException();
        if (order.getStatus().equals(OrderStatus.Cancelled)) throw new BadRequestException();
        order.setStatus(OrderStatus.Cancelled);
        orderRepository.save(order);
        return ResponseEntity.status(200).body("updated");
    }

    public ResponseEntity<?> updateOrderStatus(OrderStatus status, @NotNull @Min(0) Long orderId) {
        var orderCont = this.orderRepository.findById(orderId);
        if (orderCont.isEmpty()) throw new NotFoundException();
        var order = orderCont.get();
        order.setStatus(status);
        orderRepository.save(order);
        return ResponseEntity.status(200).body("updated");
    }
}
