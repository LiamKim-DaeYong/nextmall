package com.nextmall.common.testsupport.annotation

import com.nextmall.common.testsupport.config.TestContextConfig
import com.nextmall.common.testsupport.container.PostgresContainerInitializer
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

/**
 * Repository/데이터 레이어 통합 테스트를 위한 어노테이션.
 *
 * - PostgreSQL Testcontainer 자동 시작
 * - JPA 관련 Bean만 로드 (슬라이스 테스트)
 * - 공통 테스트 설정 자동 적용
 *
 * 사용법:
 * ```
 * @RepositoryTest
 * class UserRepositoryTest(
 *     private val userRepository: UserRepository
 * ) : FunSpec({
 *     test("사용자 저장") {
 *         val user = User(name = "test")
 *         userRepository.save(user)
 *         // ...
 *     }
 * })
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test-common.yml"])
@DataJpaTest
@ContextConfiguration(
    classes = [TestContextConfig::class],
    initializers = [PostgresContainerInitializer::class],
)
annotation class RepositoryTest
