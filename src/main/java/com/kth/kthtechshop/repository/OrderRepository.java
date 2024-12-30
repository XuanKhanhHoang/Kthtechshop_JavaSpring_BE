package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
