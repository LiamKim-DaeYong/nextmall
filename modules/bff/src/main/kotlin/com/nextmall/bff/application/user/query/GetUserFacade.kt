package com.nextmall.bff.application.user.query

interface GetUserFacade {
    suspend fun getUser(
        userId: Long,
        authorization: String,
    ): UserViewResult
}
