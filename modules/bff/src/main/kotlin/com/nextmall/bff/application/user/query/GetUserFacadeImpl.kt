package com.nextmall.bff.application.user.query

import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component

@Component
class GetUserFacadeImpl(
    private val userServiceClient: UserServiceClient,
) : GetUserFacade {
    override suspend fun getUser(
        userId: Long,
        authorization: String,
    ): UserViewResult {
        val user = userServiceClient.getUser(userId, authorization)

        return UserViewResult(
            id = user.id,
            nickname = user.nickname,
            email = user.email,
        )
    }
}
