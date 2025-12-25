package com.nextmall.bff.application.signup

import com.nextmall.bff.client.auth.AuthServiceClient
import com.nextmall.bff.client.user.UserServiceClient
import org.springframework.stereotype.Component

@Component
class SignUpFacadeImpl(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) : SignUpFacade {
    override suspend fun signUp(command: SignUpCommand): SignUpResult {
        // 1. User 생성 (PENDING)
        val userId =
            userServiceClient.createUser(
                nickname = command.nickname,
                email = null,
            )

        try {
            // 2. Auth 계정 생성
            authServiceClient.createAccount(
                userId = userId,
                provider = command.provider,
                providerAccountId = command.providerAccountId,
                password = command.password,
            )

            // 3. User 활성화
            userServiceClient.activateUser(userId)

            // 4. 토큰 발급 (로그인 처리)
            val token = authServiceClient.issueTokenForUser(userId)

            return SignUpResult(
                userId = userId,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
        } catch (ex: Exception) {
            // 5. 실패 시 보상 처리
            runCatching {
                userServiceClient.markSignupFailed(userId)
            }.onFailure {
                // TDD: 로그/메트릭 남김
                // TDD: 이후 배치/재처리 대상
            }
            throw ex
        }
    }
}
