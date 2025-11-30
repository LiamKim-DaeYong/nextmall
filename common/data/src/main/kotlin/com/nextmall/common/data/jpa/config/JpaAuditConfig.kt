package com.nextmall.common.data.jpa.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Enables JPA auditing annotations such as @CreatedDate and @LastModifiedDate.
 * Should be imported automatically through component scanning.
 */
@Configuration
@EnableJpaAuditing
class JpaAuditConfig
