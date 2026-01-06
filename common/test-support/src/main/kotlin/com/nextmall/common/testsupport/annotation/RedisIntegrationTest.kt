package com.nextmall.common.testsupport.annotation

import com.nextmall.common.redis.RedisStore
import com.nextmall.common.testsupport.container.RedisContainerInitializer
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

/**
 * Redis 통합 테스트를 위한 어노테이션.
 *
 * - Redis Testcontainer 자동 시작
 * - @RedisStore 어노테이션이 붙은 컴포넌트 자동 스캔
 * - 공통 테스트 설정 자동 적용
 *
 * 사용법:
 * ```
 * @RedisIntegrationTest
 * class RateLimitStoreTest(
 *     private val rateLimitStore: RateLimitStore
 * ) : FunSpec({
 *     test("Redis 통합 테스트") {
 *         // 실제 Redis 사용
 *     }
 * })
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test-common.yml"])
@DataRedisTest(
    includeFilters = [
        Filter(type = FilterType.ANNOTATION, classes = [RedisStore::class]),
    ],
)
@ContextConfiguration(initializers = [RedisContainerInitializer::class])
annotation class RedisIntegrationTest
