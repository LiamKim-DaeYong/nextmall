package com.nextmall.user.application.port.output

import com.nextmall.user.application.query.dto.UserView

interface UserQueryRepository {
    fun findByEmail(email: String): UserView?

    fun findById(id: Long): UserView?
}
