package com.nextmall.e2e

import com.intuit.karate.junit5.Karate
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class E2ETestRunner {
    @BeforeAll
    fun beforeAll() {
        // Docker Compose 환경이 준비되었는지 확인
        E2ETestEnvironment.waitForReady()

        // Karate에서 사용할 Gateway URL 설정
        System.setProperty("gateway.url", E2ETestEnvironment.getGatewayUrl())
    }

    @Karate.Test
    fun testAll(): Karate = Karate.run("classpath:features").relativeTo(javaClass)
}
