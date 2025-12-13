package com.nextmall.user.port.output

import com.nextmall.user.application.query.UserContext

interface UserQueryPort {
    fun findByEmail(email: String): UserContext?

    fun findById(id: Long): UserContext?
}
