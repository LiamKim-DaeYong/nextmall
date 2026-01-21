package com.nextmall.bff.client.user

import com.nextmall.bff.client.user.response.UserViewClientResponse
import reactor.core.publisher.Mono

interface UserServiceClient {
    /**
     * 사용자를 조회한다.
     *
     */
    fun getUser(
        userId: Long,
    ): Mono<UserViewClientResponse>

    /**
     * 사용자를 생성한다.
     *
     * 회원가입 유스케이스에서 최초로 호출되며,
     * 생성된 사용자는 활성화 이전의 상태(PENDING)로 생성된다.
     */
    fun createUser(
        nickname: String,
        email: String?,
    ): Mono<Long>

    /**
     * 사용자를 활성화한다.
     *
     * 인증 계정 생성이 성공한 이후 호출되며,
     * 정상적으로 서비스 이용이 가능한 상태로 전환한다.
     */
    fun activateUser(
        userId: Long,
    ): Mono<Void>

    /**
     * 회원가입 실패 상태로 표시한다.
     *
     * 회원가입 도중 오류가 발생했을 때 보상 트랜잭션 성격으로 호출되며,
     * 이후 배치 처리 또는 수동 조치를 위한 기준 상태로 사용된다.
     */
    fun markSignupFailed(
        userId: Long,
    ): Mono<Void>
}
