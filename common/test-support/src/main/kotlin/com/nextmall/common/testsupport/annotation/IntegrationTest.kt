package com.nextmall.common.testsupport.annotation

import com.nextmall.common.testsupport.config.TestContextConfig
import com.nextmall.common.testsupport.container.TestContainerInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

/**
 * 전체 통합 테스트를 위한 어노테이션.
 *
 * - PostgreSQL + Redis Testcontainers 자동 시작
 * - 전체 Spring Context 로드
 * - 공통 테스트 설정 자동 적용
 *
 * 사용법:
 * ```
 * @IntegrationTest
 * class MyIntegrationTest : FunSpec({
 *     test("통합 테스트") {
 *         // ...
 *     }
 * })
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test-common.yml"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
@Import(TestContextConfig::class)
@ContextConfiguration(initializers = [TestContainerInitializer::class])
annotation class IntegrationTest
