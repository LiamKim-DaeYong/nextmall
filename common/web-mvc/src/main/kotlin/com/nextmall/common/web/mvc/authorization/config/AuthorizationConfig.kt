package com.nextmall.common.web.mvc.authorization.config

import com.nextmall.common.web.mvc.authorization.service.AuthorizationAuditLogger
import com.nextmall.common.web.mvc.authorization.service.AuthorizationService
import com.nextmall.common.web.mvc.authorization.service.PolicyProvider
import com.nextmall.common.web.mvc.authorization.aop.PolicyEnforcementAspect
import com.nextmall.common.web.mvc.authorization.aop.ResourceAttributeResolver
import com.nextmall.common.policy.evaluator.PolicyEvaluator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthorizationConfig {
    @Bean
    @ConditionalOnMissingBean
    fun policyEvaluator(): PolicyEvaluator = PolicyEvaluator()

    @Bean
    @ConditionalOnBean(PolicyProvider::class)
    fun authorizationService(
        policyProvider: PolicyProvider,
        policyEvaluator: PolicyEvaluator,
        auditLogger: AuthorizationAuditLogger?,
    ): AuthorizationService =
        AuthorizationService(
            policyProvider = policyProvider,
            policyEvaluator = policyEvaluator,
            auditLogger = auditLogger,
        )

    @Bean
    @ConditionalOnBean(AuthorizationService::class)
    fun policyEnforcementAspect(
        authorizationService: AuthorizationService,
        resourceAttributeResolvers: List<ResourceAttributeResolver>,
    ): PolicyEnforcementAspect =
        PolicyEnforcementAspect(
            authorizationService = authorizationService,
            resourceAttributeResolvers = resourceAttributeResolvers,
        )
}
