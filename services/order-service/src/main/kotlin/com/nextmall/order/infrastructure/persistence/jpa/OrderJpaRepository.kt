package com.nextmall.order.infrastructure.persistence.jpa

import com.nextmall.order.domain.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderJpaRepository : JpaRepository<Order, Long>
