package com.nextmall.common.redis

import org.springframework.stereotype.Component

/**
 * Redis 기반 Store 컴포넌트를 표시하는 어노테이션.
 *
 * @DataRedisTest 슬라이스 테스트에서 자동으로 스캔됩니다.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class RedisStore
