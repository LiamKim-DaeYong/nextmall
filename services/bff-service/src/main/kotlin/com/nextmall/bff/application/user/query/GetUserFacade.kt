package com.nextmall.bff.application.user.query

import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetUserFacade(
    private val userServiceClient: UserServiceClient,
) {
    /**
     * 사용자 정보를 조회해 BFF 응답 모델로 변환한다.
     */
    fun getUser(
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
