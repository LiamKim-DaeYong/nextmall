package com.nextmall.orchestrator.application.signup

import com.nextmall.orchestrator.client.auth.AuthServiceClient
import com.nextmall.orchestrator.client.user.UserServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SignUpFacade(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) {
    /**
     * 사용자 생성과 인증 계정 생성을 하나의 흐름으로 처리한다.
     */
    fun signUp(command: SignUpCommand): Mono<SignUpResult> =
        userServiceClient
            .createUser(
                nickname = command.nickname,
                email = null,
            ).flatMap { userId ->
                authServiceClient
                    .createAccount(
                        userId = userId,
                        provider = command.provider,
                        providerAccountId = command.providerAccountId,
                        password = command.password,
                    ).flatMap { authAccountId ->
                        userServiceClient
                            .activateUser(userId)
                            .then(authServiceClient.issueToken(authAccountId))
                    }.map { token ->
                        SignUpResult(
                            userId = userId,
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken,
                        )
                    }.onErrorResume { ex ->
                        userServiceClient
                            .markSignupFailed(userId)
                            .then(Mono.error(ex))
                    }
            }
}
