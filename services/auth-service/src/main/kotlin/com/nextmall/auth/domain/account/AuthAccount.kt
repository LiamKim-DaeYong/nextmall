package com.nextmall.auth.domain.account

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
    name = "auth_accounts",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["provider", "provider_account_id"]),
    ],
)
class AuthAccount(
    @Id
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider,

    @Column(name = "provider_account_id", nullable = false)
    val providerAccountId: String,

    @Column(name = "password_hash", nullable = true)
    val passwordHash: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: AuthAccountStatus,
) : BaseEntity()
