package com.nextmall.orchestrator.client.user

import reactor.core.publisher.Mono

interface UserServiceClient {
    fun createUser(nickname: String, email: String?): Mono<Long>

    fun activateUser(userId: Long): Mono<Void>

    fun markSignupFailed(userId: Long): Mono<Void>
}
