package com.nextmall.bff.signup.application.command

import com.nextmall.bff.integration.auth.AuthServiceClient
import com.nextmall.bff.integration.user.UserServiceClient
import com.nextmall.bff.signup.application.result.SignUpResult
import org.springframework.stereotype.Service

@Service
class SignUpCommandHandler(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) {
    suspend fun handle(param: SignUpCommandParam): SignUpResult {
        // 1. User 생성 (PENDING)
        val userId =
            userServiceClient.createUser(
                nickname = param.nickname,
                email = null,
            )

        try {
            // 2. Auth 계정 생성
            authServiceClient.createAccount(
                userId = userId,
                provider = param.provider,
                providerAccountId = param.providerAccountId,
                password = param.password,
            )

            // 3. 토큰 발급 (로그인 처리)
            val token = authServiceClient.issueToken(userId)

            // 4. User 활성화
            userServiceClient.activateUser(userId)

            return SignUpResult(
                userId = userId,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
        } catch (ex: Exception) {
            // 5. 실패 시 보상 처리 (delete ❌, 상태 변경 ⭕)
            userServiceClient.markSignupFailed(userId)
            throw ex
        }
    }
}
