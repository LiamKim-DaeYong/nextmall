package com.nextmall.user.application.query

import com.nextmall.user.application.query.dto.UserView

interface FindUserQuery {
    fun findById(id: Long): UserView

    fun findByEmail(email: String): UserView
}
