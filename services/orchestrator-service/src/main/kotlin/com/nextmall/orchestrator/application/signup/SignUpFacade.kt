package com.nextmall.orchestrator.application.signup

import com.nextmall.orchestrator.client.auth.AuthServiceClient
import com.nextmall.orchestrator.client.user.UserServiceClient
import org.springframework.stereotype.Component

@Component
class SignUpFacade(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) {
    /**
     * 사용자 생성과 인증 계정 생성을 하나의 흐름으로 처리한다.
     */
    fun signUp(command: SignUpCommand): SignUpResult {
        val userId =
            userServiceClient.createUser(
                nickname = command.nickname,
                email = null,
            )

        return try {
            val authAccountId =
                authServiceClient.createAccount(
                    userId = userId,
                    provider = command.provider,
                    providerAccountId = command.providerAccountId,
                    password = command.password,
                )
            userServiceClient.activateUser(userId)
            val token = authServiceClient.issueToken(authAccountId)

            SignUpResult(
                userId = userId,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
        } catch (ex: Exception) {
            runCatching { userServiceClient.markSignupFailed(userId) }
            throw ex
        }
    }
}
