package com.nextmall.bff.application.user.query

import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component

@Component
class GetUserFacadeImpl(
    private val userServiceClient: UserServiceClient,
) : GetUserFacade {
    override suspend fun getUser(
        userId: Long,
    ): UserViewResult {
        val userView = userServiceClient.getUser(userId)

        return UserViewResult(
            id = userView.id,
            nickname = userView.nickname,
            email = userView.email,
        )
    }
}
