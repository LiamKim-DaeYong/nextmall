package com.nextmall.common.data.jpa.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Common JPA configuration shared across modules.
 *
 * Defines base packages for entity scanning and JPA repository discovery.
 * Extend this configuration if additional JPA-related beans are required.
 */
@Configuration
@EntityScan(basePackages = ["com.nextmall"])
@EnableJpaRepositories(basePackages = ["com.nextmall"])
class JpaConfig
