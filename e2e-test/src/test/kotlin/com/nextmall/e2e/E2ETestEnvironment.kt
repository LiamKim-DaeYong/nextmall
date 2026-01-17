package com.nextmall.e2e

import java.net.URI

/**
 * E2E 테스트 환경 설정
 *
 * Docker Compose 환경이 실행 중이라고 가정합니다.
 * 테스트 실행 전에 다음 명령으로 환경을 준비하세요:
 *
 *   docker-compose -f docker/docker-compose.e2e.yml up -d --wait
 *
 * 환경 변수:
 *   - E2E_GATEWAY_URL: Gateway URL (기본값: http://localhost:18080)
 */
object E2ETestEnvironment {
    private const val DEFAULT_GATEWAY_URL = "http://localhost:18080"

    fun getGatewayUrl(): String = System.getenv("E2E_GATEWAY_URL") ?: DEFAULT_GATEWAY_URL

    /**
     * 환경이 준비되었는지 확인 (health check)
     */
    fun waitForReady(timeoutSeconds: Int = 60) {
        val gatewayUrl = getGatewayUrl()
        val healthUrl = "$gatewayUrl/actuator/health"
        val startTime = System.currentTimeMillis()
        val timeoutMillis = timeoutSeconds * 1000L

        println("Waiting for E2E environment to be ready...")
        println("Gateway URL: $gatewayUrl")

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                val connection = URI(healthUrl).toURL().openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    println("E2E environment is ready!")
                    return
                }
            } catch (e: Exception) {
                // 아직 준비 안됨, 재시도
            }
            Thread.sleep(2000)
        }

        throw IllegalStateException(
            """
            E2E environment is not ready after ${timeoutSeconds}s.

            Please ensure Docker Compose is running:
              docker-compose -f docker/docker-compose.e2e.yml up -d --wait

            Gateway health check failed: $healthUrl
            """.trimIndent(),
        )
    }
}
