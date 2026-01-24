package com.nextmall.checkout.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface CheckoutJpaRepository : JpaRepository<CheckoutEntity, String>
