package com.nextmall.bff.client.auth

import com.nextmall.bff.client.auth.response.TokenClientResponse
import reactor.core.publisher.Mono

interface AuthServiceClient {
    /**
     * 인증 계정을 생성한다.
     *
     * 회원(User) 생성 이후 호출되며, 실제 인증 수단을 등록하는 책임은 Auth 서비스에 있다.
     */
    fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Mono<Long>

    /**
     * 인증 정보를 검증하고 토큰을 발급한다.
     *
     * 로그인 유스케이스에서 사용된다.
     */
    fun login(
        provider: AuthProvider,
        principal: String,
        credential: String?,
    ): Mono<TokenClientResponse>

    /**
     * RefreshToken을 무효화한다. (로그아웃)
     */
    fun logout(
        refreshToken: String,
    ): Mono<Void>

    /**
     * 이미 인증이 완료된 사용자에 대해 토큰만 발급한다.
     *
     * 회원가입 직후 자동 로그인 등 내부 흐름에서 사용된다.
     */
    fun issueToken(
        authAccountId: Long,
    ): Mono<TokenClientResponse>

    /**
     * RefreshToken을 사용해 토큰을 재발급한다.
     */
    fun refresh(
        refreshToken: String,
    ): Mono<TokenClientResponse>
}
