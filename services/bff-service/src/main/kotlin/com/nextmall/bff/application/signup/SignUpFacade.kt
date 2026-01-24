package com.nextmall.bff.application.signup

import com.nextmall.bff.client.orchestrator.OrchestratorServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SignUpFacade(
    private val orchestratorServiceClient: OrchestratorServiceClient,
) {
    /**
     * 회원가입 오케스트레이션을 호출한다.
     */
    fun signUp(command: SignUpCommand): Mono<SignUpResult> =
        orchestratorServiceClient
            .signUp(
                provider = command.provider,
                providerAccountId = command.providerAccountId,
                password = command.password,
                nickname = command.nickname,
            ).map { result ->
                SignUpResult(
                    userId = result.userId,
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                )
            }
}
