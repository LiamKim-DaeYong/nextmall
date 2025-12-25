package com.nextmall.bff.application.signup

interface SignUpFacade {
    /**
     * 회원가입을 처리한다.
     *
     * User 서비스에 회원 생성을 요청하고,
     * 회원 생성이 성공한 이후 Auth 서비스에 인증 계정을 생성 및 초기 토큰 발급을 요청한다.
     *
     * 본 Facade는 회원가입 전체 흐름을 조율하는 역할만 수행하며,
     * 각 도메인(User, Auth)의 비즈니스 규칙이나 정책에는 관여하지 않는다.
     */
    suspend fun signUp(command: SignUpCommand): SignUpResult
}
