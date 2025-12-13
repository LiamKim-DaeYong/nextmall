package com.nextmall.user.port.input

import com.nextmall.user.application.query.UserContext

interface FindUserQuery {
    fun findById(id: Long): UserContext
}
