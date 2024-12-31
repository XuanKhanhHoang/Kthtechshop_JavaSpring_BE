package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.order.CreateOrderDTO;
import com.kth.kthtechshop.dto.order.GetOrdersDTO;
import com.kth.kthtechshop.dto.order.OrderDTO;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.exception.ForbiddenException;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.models.Order;
import com.kth.kthtechshop.repository.OrderRepository;
import com.kth.kthtechshop.repository.OrderSpecification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDTO getOrder(Long orderId, boolean isAdmin, Long userId) {
        var orderCont = orderRepository.findById(orderId);
        if (orderCont.isEmpty()) throw new NotFoundException();
        Order order = orderCont.get();
        if (!Objects.equals(order.getUser().getId(), userId) && !isAdmin) throw new ForbiddenException();
        return new OrderDTO(order);
    }

    @Transactional
    public ListResponse<OrderDTO> getOrders(GetOrdersDTO params) {
        List<String> validOrderColumns = Arrays.asList("createAt", "receivedAt");
        if (!validOrderColumns.contains(params.getOrder_col())) {
            throw new BadRequestException("Invalid order column");
        }
        Sort sort = Sort.by(Sort.Direction.fromString(params.getOrder_type()), params.getOrder_col());
        Pageable pageable = PageRequest.of(params.getPage() != null ? params.getPage() - 1 : 0, params.getLimit() != null ? params.getLimit() : 6, sort);
        OrderStatus status = null;
        if (params.getStatusId() != null) {
            for (var st : OrderStatus.values()) {
                if (st.ordinal() == params.getStatusId()) {
                    status = st;
                    break;
                }

            }
            if (status == null) throw new BadRequestException();
        }
        Specification<Order> specification = OrderSpecification.getSpecifications(params.getUser_id(), status);
        Page<Order> ordersPage = orderRepository.findAll(specification, pageable);
        List<OrderDTO> res = ordersPage.getContent().stream()
                .map(OrderDTO::new)
                .toList();
        return new ListResponse<>(res, ordersPage.getTotalPages());
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
