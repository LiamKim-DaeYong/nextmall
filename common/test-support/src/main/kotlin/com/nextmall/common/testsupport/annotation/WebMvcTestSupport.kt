package com.nextmall.common.testsupport.annotation

import com.nextmall.common.testsupport.config.TestContextConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * WebMvc 슬라이스 테스트를 위한 어노테이션.
 *
 * - Controller 레이어 테스트 지원
 * - 공통 테스트 설정 자동 적용
 *
 * 사용법:
 * ```
 * @WebMvcTestSupport
 * class MyControllerTest {
 *     // ...
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test-common.yml"])
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContextConfig::class])
annotation class WebMvcTestSupport
