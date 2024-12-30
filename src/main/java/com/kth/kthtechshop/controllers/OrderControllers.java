package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.order.OrderDTO;
import com.kth.kthtechshop.services.OrderService;
import com.kth.kthtechshop.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/private")
public class OrderControllers {

    private final OrderService orderService;

    @Autowired
    public OrderControllers(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("get_orders")
    public ListResponse<OrderDTO> getAllProducts() {
        Long userId = SecurityUtil.getUserId();
        return orderService.getOrder(userId);
    }

}
