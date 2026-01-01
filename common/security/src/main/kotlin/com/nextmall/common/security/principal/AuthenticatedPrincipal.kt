package com.nextmall.common.security.principal

/**
 * 인증(AuthN)이 완료된 이후의 사용자 식별 정보를 표현한다.
 *
 * 이 객체는 "인증"만을 위한 결과물이며,
 * 인가(AuthZ), 권한(Role), 스코프(Scope) 정보는 포함하지 않는다.
 */
data class AuthenticatedPrincipal(
    val subject: String,
    val userId: String,
)
