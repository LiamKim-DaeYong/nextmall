package com.nextmall.common.testsupport.container

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * PostgreSQL Testcontainer Singleton.
 *
 * 전체 테스트 스위트에서 하나의 컨테이너만 공유하여 테스트 속도를 최적화합니다.
 * JVM이 종료될 때 컨테이너가 자동으로 정리됩니다.
 *
 * 사용법:
 * ```
 * @SpringBootTest
 * class MyIntegrationTest {
 *     companion object {
 *         @JvmStatic
 *         @DynamicPropertySource
 *         fun properties(registry: DynamicPropertyRegistry) {
 *             PostgresTestContainer.configureProperties(registry)
 *         }
 *     }
 * }
 * ```
 */
object PostgresTestContainer {
    private const val IMAGE_NAME = "postgres:16-alpine"
    private const val DATABASE_NAME = "testdb"
    private const val USERNAME = "test"
    private const val PASSWORD = "test"

    val instance: PostgreSQLContainer by lazy {
        PostgreSQLContainer(DockerImageName.parse(IMAGE_NAME))
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withReuse(true)
            .also { it.start() }
    }

    val jdbcUrl: String get() = instance.jdbcUrl
    val username: String get() = instance.username
    val password: String get() = instance.password

    /**
     * Spring DynamicPropertyRegistry에 데이터소스 프로퍼티를 등록합니다.
     */
    fun configureProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.datasource.url") { jdbcUrl }
        registry.add("spring.datasource.username") { username }
        registry.add("spring.datasource.password") { password }
        registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
    }
}
