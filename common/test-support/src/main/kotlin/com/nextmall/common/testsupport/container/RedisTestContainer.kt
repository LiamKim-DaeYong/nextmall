package com.nextmall.common.testsupport.container

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * Redis Testcontainer Singleton.
 *
 * 전체 테스트 스위트에서 하나의 컨테이너만 공유하여 테스트 속도를 최적화합니다.
 * JVM이 종료될 때 컨테이너가 자동으로 정리됩니다.
 */
object RedisTestContainer {
    private const val IMAGE_NAME = "redis:7-alpine"
    private const val REDIS_PORT = 6379

    val instance: GenericContainer<*> by lazy {
        GenericContainer(DockerImageName.parse(IMAGE_NAME))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true)
            .also { it.start() }
    }

    val host: String get() = instance.host
    val port: Int get() = instance.getMappedPort(REDIS_PORT)

    /**
     * Spring DynamicPropertyRegistry에 Redis 프로퍼티를 등록합니다.
     */
    fun configureProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.data.redis.host") { host }
        registry.add("spring.data.redis.port") { port }
    }
}
