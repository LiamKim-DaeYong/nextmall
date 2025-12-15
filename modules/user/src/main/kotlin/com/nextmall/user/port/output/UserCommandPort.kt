package com.nextmall.user.port.output

import com.nextmall.user.domain.entity.User

interface UserCommandPort {
    fun save(user: User): User

    fun findById(userId: Long): User?
}
