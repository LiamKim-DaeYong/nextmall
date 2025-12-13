package com.nextmall.common.testsupport

import com.nextmall.common.identifier.config.IdentifierConfig
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.nextmall"])
@EntityScan(basePackages = ["com.nextmall"])
@Import(IdentifierConfig::class)
@EnableJpaAuditing
class TestJpaConfiguration
