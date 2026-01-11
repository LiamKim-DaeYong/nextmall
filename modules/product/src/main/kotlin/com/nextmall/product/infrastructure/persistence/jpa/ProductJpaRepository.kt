package com.nextmall.product.infrastructure.persistence.jpa

import com.nextmall.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<Product, Long>
