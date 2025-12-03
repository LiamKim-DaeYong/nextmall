package com.nextmall.common.testsupport

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContextConfiguration::class])
annotation class WebMvcTestSupport
