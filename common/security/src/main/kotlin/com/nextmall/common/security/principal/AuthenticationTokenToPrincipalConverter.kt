package com.nextmall.common.security.principal

/**
 * 인증 과정에서 토큰 정보를 기반으로
 * AuthenticatedPrincipal을 생성하는 역할을 한다.
 *
 * 이 Converter는 인증(AuthN) 범위만 책임지며, 인가(AuthZ) 정보는 다루지 않는다.
 */
fun interface AuthenticationTokenToPrincipalConverter<T> {

    fun convert(token: T): AuthenticatedPrincipal
}
