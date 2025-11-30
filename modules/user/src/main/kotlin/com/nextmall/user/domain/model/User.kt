package com.nextmall.user.domain.model

import com.nextmall.common.data.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    val id: Long,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true)
    val password: String? = null,

    @Column(nullable = false)
    val nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @Column(nullable = true)
    val providerId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.BUYER,
) : BaseEntity()
