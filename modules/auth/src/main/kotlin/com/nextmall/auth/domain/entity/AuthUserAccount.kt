package com.nextmall.auth.domain.entity

import com.nextmall.auth.domain.model.AuthProvider
import com.nextmall.common.data.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "auth_user_accounts",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["provider", "providerAccountId"]),
    ],
)
class AuthUserAccount(
    @Id
    val id: Long,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider,

    @Column(nullable = false)
    val providerAccountId: String,

    @Column(nullable = true)
    val passwordHash: String? = null,
) : BaseEntity()
