package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.VNPayReturnResponseDTO;
import com.kth.kthtechshop.dto.order.*;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.services.OrderService;
import com.kth.kthtechshop.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/order/private")
public class OrderControllers {

    private final OrderService orderService;

    @Autowired
    public OrderControllers(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("get_orders")
    public ListResponse<OrderDTO> getOrders(@Valid @RequestParam GetOrdersDTO params) {
        Long userId = SecurityUtil.getUserId();
        params.setUser_id(userId);
        return orderService.getOrders(params);
    }

    @GetMapping("get_order")
    public OrderDTO getOrder(@RequestParam Long orderId) {
        if (orderId <= 0) throw new BadRequestException();
        boolean isAdmin = SecurityUtil.isAdmin();
        Long userId = SecurityUtil.getUserId();
        return orderService.getOrder(orderId, isAdmin, userId);
    }

    @PostMapping("create_order")
    public CreateOrderResponseDTO createOrder(@Valid @RequestBody CreateOrderDTO newOrder, HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId();
        String ipAddr = SecurityUtil.getClientIp(request);
        return orderService.createOrder(ipAddr, userId, newOrder);
    }

    @GetMapping("get_payment_url")
    public String getPaymentUrl(@RequestParam Map<String, String> query, HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId();
        String ipAddr = SecurityUtil.getClientIp(request);
        String oid = query.get("order_id");
        if (oid == null) throw new BadRequestException();
        try {
            Long orderId = Long.parseLong(oid);
            return orderService.getPaymentUrl(userId, ipAddr, orderId);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }
    }

    @GetMapping("admin_get_full_orders")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public ListResponse<OrderDTO> getFullOrderAdmin(@Valid @RequestParam GetOrdersDTO params) {
        return orderService.getOrders(params);
    }

    @PostMapping("cancel_order")
    public ResponseEntity<?> cancelOrder(@RequestBody Map<String, String> body) {
        String orderIdStr = body.get("order_id");
        long orderId;
        try {
            orderId = Long.parseLong(orderIdStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }
        boolean isAdmin = SecurityUtil.isAdmin();
        long userId = SecurityUtil.getUserId();
        return orderService.cancelOrder(isAdmin, userId, orderId);
    }

    @PostMapping("change_order_status")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public ResponseEntity<?> changeOrderStatus(@RequestBody @Valid UpdateOrderStatusDTO order) {
        OrderStatus status = null;
        for (var status1 : OrderStatus.values()) {
            if (status1.ordinal() == order.getStatus_id()) {
                status = status1;
                break;
            }
        }
        if (status == null) throw new BadRequestException();
        return orderService.updateOrderStatus(status, order.getOrder_id());
    }

    @GetMapping("vnpreturn")
    public VNPayReturnResponseDTO handleVnPayReturnResponse(@RequestParam Map<String, String> vnpParams) {
        return orderService.handleVnPayReturnResponse(vnpParams);
    }
}
