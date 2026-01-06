package com.nextmall.common.testsupport.container

import org.springframework.test.context.DynamicPropertyRegistry

/**
 * 테스트에 필요한 모든 컨테이너를 통합 관리합니다.
 *
 * 사용법:
 * ```
 * @SpringBootTest
 * class MyIntegrationTest {
 *     companion object {
 *         @JvmStatic
 *         @DynamicPropertySource
 *         fun properties(registry: DynamicPropertyRegistry) {
 *             TestContainers.configureAll(registry)
 *         }
 *     }
 * }
 * ```
 */
object TestContainers {
    /**
     * PostgreSQL과 Redis 모든 컨테이너의 프로퍼티를 등록합니다.
     */
    fun configureAll(registry: DynamicPropertyRegistry) {
        PostgresTestContainer.configureProperties(registry)
        RedisTestContainer.configureProperties(registry)
    }

    /**
     * PostgreSQL만 필요한 경우 사용합니다.
     */
    fun configurePostgres(registry: DynamicPropertyRegistry) {
        PostgresTestContainer.configureProperties(registry)
    }

    /**
     * Redis만 필요한 경우 사용합니다.
     */
    fun configureRedis(registry: DynamicPropertyRegistry) {
        RedisTestContainer.configureProperties(registry)
    }
}
