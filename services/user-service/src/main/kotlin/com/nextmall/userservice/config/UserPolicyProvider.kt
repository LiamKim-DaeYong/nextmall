package com.nextmall.userservice.config

import com.nextmall.common.authorization.config.AuthorizationConfig
import com.nextmall.common.authorization.service.PolicyProvider
import com.nextmall.common.policy.factory.policy
import com.nextmall.common.policy.model.Operator
import com.nextmall.common.policy.model.Policy
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(AuthorizationConfig::class)
class UserPolicyProvider : PolicyProvider {
    private val policies: Map<String, Policy> = buildPolicies()

    override fun getPolicy(resource: String, action: String): Policy? = policies["$resource:$action"]

    private fun buildPolicies(): Map<String, Policy> =
        listOf(
            // 사용자 조회 정책
            policy("user", "read") {
                name = "사용자 조회 정책"
                description = "관리자는 전체 조회, 일반 사용자는 본인만 조회 가능"

                allow("관리자 전체 조회") {
                    condition("subject.roles", Operator.CONTAINS, "ADMIN")
                }

                allow("본인 정보 조회") {
                    conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                }
            },

            // 사용자 생성 정책 (회원가입은 누구나 가능)
            policy("user", "create") {
                name = "사용자 생성 정책"

                allow("회원가입 허용") {
                    condition("subject.authenticated", Operator.EQUALS, false)
                }

                allow("관리자 계정 생성") {
                    condition("subject.roles", Operator.CONTAINS, "ADMIN")
                }
            },

            // 사용자 수정 정책
            policy("user", "update") {
                name = "사용자 수정 정책"

                allow("관리자 수정 권한") {
                    condition("subject.roles", Operator.CONTAINS, "ADMIN")
                }

                allow("본인 정보 수정") {
                    conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                }
            },

            // 사용자 삭제 정책
            policy("user", "delete") {
                name = "사용자 삭제 정책"
                description = "관리자만 삭제 가능, 본인은 탈퇴 처리"

                allow("관리자 삭제 권한") {
                    condition("subject.roles", Operator.CONTAINS, "ADMIN")
                }

                allow("본인 탈퇴") {
                    conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                }
            },
        ).associateBy { it.id }
}
