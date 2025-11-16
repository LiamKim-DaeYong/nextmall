package com.nextmall.common.testsupport

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = [TestContextConfiguration::class, TestJpaConfiguration::class])
annotation class RepositoryTest
