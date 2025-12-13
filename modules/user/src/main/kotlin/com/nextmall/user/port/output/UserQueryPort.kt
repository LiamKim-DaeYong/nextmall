package com.nextmall.user.port.output

import com.nextmall.user.application.query.UserContext

interface UserQueryPort {
    fun findById(id: Long): UserContext?
}
