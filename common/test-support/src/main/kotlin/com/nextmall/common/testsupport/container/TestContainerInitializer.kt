package com.nextmall.common.testsupport.container

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.support.TestPropertySourceUtils

/**
 * PostgreSQL + Redis Testcontainers를 위한 ApplicationContextInitializer.
 *
 * 어노테이션에서 이 Initializer를 사용하면 테스트 클래스에서
 * @DynamicPropertySource 없이도 자동으로 컨테이너 프로퍼티가 주입됩니다.
 */
class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val postgres = PostgresTestContainer.instance
        val redis = RedisTestContainer.instance

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            applicationContext,
            "spring.datasource.url=${postgres.jdbcUrl}",
            "spring.datasource.username=${postgres.username}",
            "spring.datasource.password=${postgres.password}",
            "spring.datasource.driver-class-name=org.postgresql.Driver",
            "spring.data.redis.host=${redis.host}",
            "spring.data.redis.port=${redis.getMappedPort(6379)}",
        )
    }
}

/**
 * PostgreSQL만 사용하는 테스트를 위한 Initializer.
 */
class PostgresContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val postgres = PostgresTestContainer.instance

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            applicationContext,
            "spring.datasource.url=${postgres.jdbcUrl}",
            "spring.datasource.username=${postgres.username}",
            "spring.datasource.password=${postgres.password}",
            "spring.datasource.driver-class-name=org.postgresql.Driver",
        )
    }
}

/**
 * Redis만 사용하는 테스트를 위한 Initializer.
 */
class RedisContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val redis = RedisTestContainer.instance

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            applicationContext,
            "spring.data.redis.host=${redis.host}",
            "spring.data.redis.port=${redis.getMappedPort(6379)}",
        )
    }
}
