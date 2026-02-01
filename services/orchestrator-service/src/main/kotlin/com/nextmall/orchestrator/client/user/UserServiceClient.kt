package com.nextmall.orchestrator.client.user

interface UserServiceClient {
    fun createUser(nickname: String, email: String?): Long

    fun activateUser(userId: Long)

    fun markSignupFailed(userId: Long)
}
