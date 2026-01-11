package com.nextmall.common.authorization.service

import com.nextmall.common.authorization.context.AuthorizationContext
import com.nextmall.common.authorization.exception.AccessDeniedException
import com.nextmall.common.authorization.exception.PolicyNotFoundException
import com.nextmall.common.policy.evaluator.PolicyEvaluator
import com.nextmall.common.policy.factory.policy
import com.nextmall.common.policy.model.Operator
import com.nextmall.common.policy.model.Policy
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AuthorizationServiceTest :
    FunSpec({

        val policyEvaluator = PolicyEvaluator()

        context("AuthorizationService + DSL Policy 통합") {

            test("관리자는 사용자 조회가 허용된다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                val context =
                    AuthorizationContext(
                        userId = "admin-1",
                        roles = setOf("ADMIN"),
                        resource = "user",
                        action = "read",
                    )

                val result = service.evaluate(context)

                result.isAllowed shouldBe true
            }

            test("일반 사용자는 본인 정보만 조회 가능하다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                // 본인 조회 → 허용
                val selfContext =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "user",
                        resourceId = "user-123",
                        resourceAttributes = mapOf("id" to "user-123"),
                        action = "read",
                    )
                service.evaluate(selfContext).isAllowed shouldBe true

                // 타인 조회 → 거부
                val otherContext =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "user",
                        resourceId = "user-456",
                        resourceAttributes = mapOf("id" to "user-456"),
                        action = "read",
                    )
                service.evaluate(otherContext).isDenied shouldBe true
            }

            test("authorize()는 거부 시 AccessDeniedException을 던진다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                val context =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "user",
                        resourceId = "user-456",
                        resourceAttributes = mapOf("id" to "user-456"),
                        action = "read",
                    )

                val exception =
                    shouldThrow<AccessDeniedException> {
                        service.authorize(context)
                    }

                exception.resource shouldBe "user"
                exception.action shouldBe "read"
            }

            test("정책이 없으면 PolicyNotFoundException을 던진다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                val context =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "unknown",
                        action = "unknown",
                    )

                shouldThrow<PolicyNotFoundException> {
                    service.evaluate(context)
                }
            }

            test("관리자는 사용자 삭제가 허용된다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                val context =
                    AuthorizationContext(
                        userId = "admin-1",
                        roles = setOf("ADMIN"),
                        resource = "user",
                        resourceId = "user-456",
                        resourceAttributes = mapOf("id" to "user-456"),
                        action = "delete",
                    )

                service.evaluate(context).isAllowed shouldBe true
            }

            test("일반 사용자는 본인만 탈퇴(삭제) 가능하다") {
                val policyProvider = testPolicyProvider()
                val service = AuthorizationService(policyProvider, policyEvaluator)

                // 본인 탈퇴 → 허용
                val selfContext =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "user",
                        resourceId = "user-123",
                        resourceAttributes = mapOf("id" to "user-123"),
                        action = "delete",
                    )
                service.evaluate(selfContext).isAllowed shouldBe true

                // 타인 삭제 → 거부
                val otherContext =
                    AuthorizationContext(
                        userId = "user-123",
                        roles = setOf("USER"),
                        resource = "user",
                        resourceId = "user-456",
                        resourceAttributes = mapOf("id" to "user-456"),
                        action = "delete",
                    )
                service.evaluate(otherContext).isDenied shouldBe true
            }
        }
    }) {
    companion object {
        /**
         * DSL로 정의한 테스트용 PolicyProvider
         */
        fun testPolicyProvider(): PolicyProvider {
            val policies =
                mapOf(
                    "user:read" to
                        policy("user", "read") {
                            name = "사용자 조회 정책"

                            allow("관리자 전체 조회") {
                                condition("subject.roles", Operator.CONTAINS, "ADMIN")
                            }

                            allow("본인 정보 조회") {
                                conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                            }
                        },
                    "user:update" to
                        policy("user", "update") {
                            name = "사용자 수정 정책"

                            allow("관리자 수정 권한") {
                                condition("subject.roles", Operator.CONTAINS, "ADMIN")
                            }

                            allow("본인 정보 수정") {
                                conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                            }
                        },
                    "user:delete" to
                        policy("user", "delete") {
                            name = "사용자 삭제 정책"

                            allow("관리자 삭제 권한") {
                                condition("subject.roles", Operator.CONTAINS, "ADMIN")
                            }

                            allow("본인 탈퇴") {
                                conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                            }
                        },
                )

            return object : PolicyProvider {
                override fun getPolicy(resource: String, action: String): Policy? = policies["$resource:$action"]
            }
        }
    }
}
