package com.nextmall.user.domain.repository

import com.nextmall.user.application.query.dto.UserView

interface UserQueryRepository {
    fun findByEmail(email: String): UserView?

    fun findById(id: Long): UserView?
}
