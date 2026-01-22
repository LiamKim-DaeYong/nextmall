package com.nextmall.bff.application.signup

import com.nextmall.bff.client.auth.AuthServiceClient
import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SignUpFacadeImpl(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) : SignUpFacade {
    override fun signUp(command: SignUpCommand): Mono<SignUpResult> =
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
