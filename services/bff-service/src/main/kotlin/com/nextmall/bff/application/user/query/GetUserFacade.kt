package com.nextmall.bff.application.user.query

import reactor.core.publisher.Mono

interface GetUserFacade {
    fun getUser(
        userId: Long,
    ): Mono<UserViewResult>
}
