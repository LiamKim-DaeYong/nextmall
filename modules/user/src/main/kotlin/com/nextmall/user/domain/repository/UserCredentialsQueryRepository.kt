package com.nextmall.user.domain.repository

import com.nextmall.user.application.query.dto.UserCredentials

interface UserCredentialsQueryRepository {
    fun findById(id: Long): UserCredentials?

    fun findByEmail(email: String): UserCredentials?
}
