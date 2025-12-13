package com.nextmall.user.domain.entity

import com.nextmall.common.data.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    val id: Long,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = true)
    val email: String?,
) : BaseEntity()
