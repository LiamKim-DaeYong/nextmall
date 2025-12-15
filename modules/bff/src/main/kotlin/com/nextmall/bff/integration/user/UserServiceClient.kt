package com.nextmall.bff.integration.user

interface UserServiceClient {
    suspend fun createUser(
        nickname: String,
        email: String?,
    ): Long

    suspend fun activateUser(
        userId: Long,
    )

    suspend fun markSignupFailed(
        userId: Long,
    )
}
