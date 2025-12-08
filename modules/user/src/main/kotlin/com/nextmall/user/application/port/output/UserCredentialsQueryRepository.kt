package com.nextmall.user.application.port.output

import com.nextmall.user.application.query.dto.UserCredentials

interface UserCredentialsQueryRepository {
    fun findById(id: Long): UserCredentials?

    fun findByEmail(email: String): UserCredentials?
}
