package com.nextmall.user.infrastructure.persistence.jpa

import com.nextmall.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long>
