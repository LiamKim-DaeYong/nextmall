package com.nextmall.order.infrastructure.persistence.jpa

import com.nextmall.order.domain.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderJpaRepository : JpaRepository<OrderEntity, String>

