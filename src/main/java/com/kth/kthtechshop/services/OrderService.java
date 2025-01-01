package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.VNPayReturnResponseDTO;
import com.kth.kthtechshop.dto.order.*;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.enums.PaymentMethod;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.exception.ForbiddenException;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.models.Order;
import com.kth.kthtechshop.models.OrderListProduct;
import com.kth.kthtechshop.models.ProductOption;
import com.kth.kthtechshop.repository.OrderRepository;
import com.kth.kthtechshop.repository.OrderSpecification;
import com.kth.kthtechshop.repository.ProductOptionRepository;
import com.kth.kthtechshop.repository.UserRepository;
import com.kth.kthtechshop.utils.DeliveryUtils;
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

import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductOptionRepository productOptionRepository, UserRepository userRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productOptionRepository = productOptionRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
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


    public CreateOrderResponseDTO createOrder(String ipAddr, Long userId, @Valid CreateOrderDTO newOrder) {
        PaymentMethod paymentMethod = PaymentMethod.getPaymentMethod(newOrder.getPaymentMethodId());
        if (paymentMethod == null) throw new BadRequestException();
        String[] addressParts = newOrder.getAddress().split(",", 3);
        int districtId = Integer.parseInt(addressParts[1]);
        Integer deliveryFee = DeliveryUtils.getDeliveryFee(districtId);
        if (deliveryFee == null) throw new BadRequestException();
        List<ProductOption> options = productOptionRepository.findAllById(newOrder.getData().stream().map(CreateOrderItem::getOptionId).toList());
        if (options.isEmpty()) {
            throw new BadRequestException();
        }
        var userCont = userRepository.findById(userId);
        if (userCont.isEmpty()) throw new ForbiddenException();
        Order order = new Order(deliveryFee, newOrder.getAddress(), paymentMethod, userCont.get());
        long totalValue = 0;
        List<OrderListProduct> orderListItems = new ArrayList<>();
        for (ProductOption option : options) {
            double priceSell = (double) (option.getSellPrice() * (100 - option.getDiscount())) / 100;
            totalValue += (long) (priceSell * option.getAmount());
            orderListItems.add(OrderListProduct.builder().quantity(option.getAmount()).sellingPrice((long) priceSell).discount(option.getDiscount()).productOption(option).build());
        }
        order.setOrderProductList(orderListItems);
        if (paymentMethod.equals(PaymentMethod.VNpay)) order.setStatus(OrderStatus.WaitingForPay);
        var res = orderRepository.save(order);
        if (paymentMethod.equals(PaymentMethod.Cash))
            return new CreateOrderResponseDTO(null, "order created.", res.getId());
        return new CreateOrderResponseDTO(paymentService.createPayment(ipAddr, res.getId(), totalValue), "order created.", res.getId());
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

    public String getPaymentUrl(Long userId, String ipAddr, Long orderId) {
        var orderCont = orderRepository.findById(orderId);
        if (orderCont.isEmpty()) throw new NotFoundException();
        Order order = orderCont.get();
        if (!Objects.equals(order.getUser().getId(), userId)) throw new ForbiddenException();
        List<ProductOption> options = productOptionRepository.findAllById(order.getOrderProductList().stream().map(item -> item.getProductOption().getId()).toList());
        if (options.isEmpty()) {
            throw new BadRequestException();
        }
        long totalPrice = getTotalPrice(options);
        return String.format("redirect:%s", paymentService.createPayment(ipAddr, orderId, totalPrice));
    }

    private long getTotalPrice(List<ProductOption> options) {
        long totalValue = 0;
        for (ProductOption option : options) {
            double priceSell = (double) (option.getSellPrice() * (100 - option.getDiscount())) / 100;
            totalValue += (long) (priceSell * option.getAmount());
        }
        return totalValue;
    }

    public VNPayReturnResponseDTO handleVnPayReturnResponse(Map<String, String> vnpParams) {
        var res = paymentService.handleVnPayReturn(vnpParams);
        if (res.getStatus() == 0) {
            var orderC = orderRepository.findById(res.getOrder_id());
            if (orderC.isEmpty()) return new VNPayReturnResponseDTO(res.getOrder_id(), 99);
            var order = orderC.get();
            order.setStatus(OrderStatus.WaitingForDelivering);
            orderRepository.save(order);
            return new VNPayReturnResponseDTO(res.getOrder_id(), 0);
        }
        return res;
    }
}
