package com.nextmall.common.testsupport.annotation

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import kotlin.reflect.KClass

/**
 * Controller 슬라이스 테스트를 위한 어노테이션.
 *
 * - Controller 레이어만 로드 (슬라이스 테스트)
 * - 공통 테스트 설정 자동 적용
 *
 * 사용법:
 * ```
 * @ControllerTest(controllers = [MyController::class])
 * class MyControllerTest {
 *     @Autowired
 *     lateinit var mockMvc: MockMvc
 *
 *     @Test
 *     fun `컨트롤러 테스트`() {
 *         // ...
 *     }
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test-common.yml"])
@WebMvcTest
annotation class ControllerTest(
    @get:AliasFor(annotation = WebMvcTest::class, attribute = "controllers")
    val controllers: Array<KClass<*>> = [],
)
