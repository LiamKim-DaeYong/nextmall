package com.nextmall.user.domain.entity

import com.nextmall.common.data.jpa.BaseEntity
import com.nextmall.user.domain.model.UserStatus
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

    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Column(name = "email", nullable = true)
    val email: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserStatus,
) : BaseEntity() {
    fun activate() {
        this.status = UserStatus.ACTIVE
    }

    fun markSignupFailed() {
        this.status = UserStatus.SIGNUP_FAILED
    }
}
