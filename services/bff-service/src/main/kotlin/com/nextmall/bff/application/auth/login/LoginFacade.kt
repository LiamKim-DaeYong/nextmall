package com.nextmall.bff.application.auth.login

import com.nextmall.bff.application.auth.token.TokenResult

interface LoginFacade {
    /**
     * 사용자 로그인을 처리한다.
     *
     * 클라이언트로부터 전달된 인증 정보로 로그인을 시도하며,
     * 토큰 발급 여부 및 인증 실패 판단은 Auth 서비스에 위임한다.
     */
    suspend fun login(command: LoginCommand): TokenResult
}
