package com.nextmall.user.config

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
                    condition(SUBJECT_ROLES, Operator.CONTAINS, ROLE_ADMIN)
                }

                allow("본인 정보 조회") {
                    conditionRef(SUBJECT_USER_ID, Operator.EQUALS, RESOURCE_ID)
                }
            },

            // 사용자 생성 정책 (관리자만 가능)
            policy("user", "create") {
                name = "사용자 생성 정책"
                description = "관리자만 계정 생성 가능. 회원가입은 BFF permitAll()로 처리"

                allow("관리자 계정 생성") {
                    condition(SUBJECT_ROLES, Operator.CONTAINS, ROLE_ADMIN)
                }
            },

            // 사용자 수정 정책
            policy("user", "update") {
                name = "사용자 수정 정책"

                allow("관리자 수정 권한") {
                    condition(SUBJECT_ROLES, Operator.CONTAINS, ROLE_ADMIN)
                }

                allow("본인 정보 수정") {
                    conditionRef(SUBJECT_USER_ID, Operator.EQUALS, RESOURCE_ID)
                }
            },

            // 사용자 삭제 정책
            policy("user", "delete") {
                name = "사용자 삭제 정책"
                description = "관리자만 삭제 가능, 본인은 탈퇴 처리"

                allow("관리자 삭제 권한") {
                    condition(SUBJECT_ROLES, Operator.CONTAINS, ROLE_ADMIN)
                }

                allow("본인 탈퇴") {
                    conditionRef(SUBJECT_USER_ID, Operator.EQUALS, RESOURCE_ID)
                }
            },
        ).associateBy { it.id }

    companion object {
        private const val SUBJECT_USER_ID = "subject.userId"
        private const val SUBJECT_ROLES = "subject.roles"
        private const val RESOURCE_ID = "resource.id"
        private const val ROLE_ADMIN = "ADMIN"
    }
}
