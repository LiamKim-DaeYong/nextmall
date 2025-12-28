package com.nextmall.bff.client.auth

interface AuthAccountClient {
    fun getRootUserId(authAccountId: Long): Long
}
