package com.nextmall.bff.application.auth.token

interface TokenFacade {
    /**
     * 로그아웃을 처리한다.
     *
     * Refresh Token을 폐기(revoke)하여
     * 이후 토큰 재발급이 불가능하도록 한다.
     *
     * Access Token은 서버에서 관리하지 않으며,
     * 클라이언트가 직접 폐기하는 것을 전제로 한다.
     */
    suspend fun logout(refreshToken: String)

    /**
     * Refresh Token을 이용해 새로운 토큰을 발급한다.
     *
     * Access Token 만료 시 호출되며,
     * Refresh Token의 유효성 검증 및 회전(rotation) 정책은
     * Auth 서비스의 책임이다.
     */
    suspend fun refresh(refreshToken: String): TokenResult
}
