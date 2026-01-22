package com.nextmall.bff.application.user.query

import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetUserFacadeImpl(
    private val userServiceClient: UserServiceClient,
) : GetUserFacade {
    override fun getUser(
        userId: Long,
    ): Mono<UserViewResult> =
        userServiceClient
            .getUser(userId)
            .map { userView ->
                UserViewResult(
                    id = userView.id,
                    nickname = userView.nickname,
                    email = userView.email,
                )
            }
}
