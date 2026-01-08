package com.nextmall.common.policy.evaluator

import com.nextmall.common.policy.context.PolicyContext
import com.nextmall.common.policy.context.Resource
import com.nextmall.common.policy.context.Subject
import com.nextmall.common.policy.model.Condition
import com.nextmall.common.policy.model.Effect
import com.nextmall.common.policy.model.Operator
import com.nextmall.common.policy.model.Policy
import com.nextmall.common.policy.model.Rule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PolicyEvaluatorTest :
    FunSpec({

        val evaluator = PolicyEvaluator()

        context("기본 동작") {

            test("ALLOW 규칙이 매칭되면 허용한다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "ADMIN"),
                                        ),
                                ),
                            ),
                    )

                val context =
                    context(
                        subject = Subject(userId = "user-1", roles = setOf("ADMIN")),
                    )

                val result = evaluator.evaluate(policy, context)

                result.isAllowed shouldBe true
            }

            test("DENY 규칙이 매칭되면 거부한다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.DENY,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "BLOCKED"),
                                        ),
                                ),
                            ),
                    )

                val context =
                    context(
                        subject = Subject(userId = "user-1", roles = setOf("BLOCKED")),
                    )

                val result = evaluator.evaluate(policy, context)

                result.isDenied shouldBe true
            }

            test("매칭되는 규칙이 없으면 기본 거부한다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "ADMIN"),
                                        ),
                                ),
                            ),
                    )

                val context =
                    context(
                        subject = Subject(userId = "user-1", roles = setOf("USER")),
                    )

                val result = evaluator.evaluate(policy, context)

                result.isDenied shouldBe true
                result.reason shouldBe "매칭되는 ALLOW 규칙 없음 (기본 거부)"
            }

            test("비활성화된 정책은 거부한다") {
                val policy =
                    policy(
                        enabled = false,
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "ADMIN"),
                                        ),
                                ),
                            ),
                    )

                val context =
                    context(
                        subject = Subject(userId = "user-1", roles = setOf("ADMIN")),
                    )

                val result = evaluator.evaluate(policy, context)

                result.isDenied shouldBe true
                result.reason shouldBe "정책이 비활성화됨"
            }
        }

        context("DENY 우선 규칙") {

            test("ALLOW와 DENY 모두 매칭되면 DENY가 우선한다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    id = "allow-admin",
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "ADMIN"),
                                        ),
                                ),
                                rule(
                                    id = "deny-blocked",
                                    effect = Effect.DENY,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "BLOCKED"),
                                        ),
                                ),
                            ),
                    )

                val context =
                    context(
                        subject = Subject(userId = "user-1", roles = setOf("ADMIN", "BLOCKED")),
                    )

                val result = evaluator.evaluate(policy, context)

                result.isDenied shouldBe true
                result.matchedRuleId shouldBe "deny-blocked"
            }
        }

        context("valueRef 동적 참조") {

            test("subject 속성을 참조하여 비교한다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "SETTLEMENT_MANAGER"),
                                            Condition(
                                                "resource.sellerId",
                                                Operator.IN,
                                                valueRef = "subject.assignedSellers",
                                            ),
                                        ),
                                ),
                            ),
                    )

                // 담당 셀러의 정산 → 허용
                val allowedContext =
                    context(
                        subject =
                            Subject(
                                userId = "user-1",
                                roles = setOf("SETTLEMENT_MANAGER"),
                                attributes = mapOf("assignedSellers" to listOf("seller-A", "seller-B")),
                            ),
                        resource =
                            Resource(
                                type = "settlement",
                                attributes = mapOf("sellerId" to "seller-A"),
                            ),
                    )
                evaluator.evaluate(policy, allowedContext).isAllowed shouldBe true

                // 담당 아닌 셀러의 정산 → 거부
                val deniedContext =
                    context(
                        subject =
                            Subject(
                                userId = "user-1",
                                roles = setOf("SETTLEMENT_MANAGER"),
                                attributes = mapOf("assignedSellers" to listOf("seller-A", "seller-B")),
                            ),
                        resource =
                            Resource(
                                type = "settlement",
                                attributes = mapOf("sellerId" to "seller-C"),
                            ),
                    )
                evaluator.evaluate(policy, deniedContext).isDenied shouldBe true
            }
        }

        context("Operator 동작") {

            test("EQUALS: 값이 같으면 true") {
                val policy =
                    policyWithCondition(
                        Condition("resource.status", Operator.EQUALS, value = "PENDING"),
                    )

                evaluator.evaluate(policy, contextWithResource("status" to "PENDING")).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("status" to "COMPLETED")).isAllowed shouldBe false
            }

            test("NOT_EQUALS: 값이 다르면 true") {
                val policy =
                    policyWithCondition(
                        Condition("resource.status", Operator.NOT_EQUALS, value = "COMPLETED"),
                    )

                evaluator.evaluate(policy, contextWithResource("status" to "PENDING")).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("status" to "COMPLETED")).isAllowed shouldBe false
            }

            test("IN: 값이 컬렉션에 포함되면 true") {
                val policy =
                    policyWithCondition(
                        Condition("resource.status", Operator.IN, value = listOf("PENDING", "PROCESSING")),
                    )

                evaluator.evaluate(policy, contextWithResource("status" to "PENDING")).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("status" to "COMPLETED")).isAllowed shouldBe false
            }

            test("CONTAINS: 컬렉션이 값을 포함하면 true") {
                val policy =
                    policyWithCondition(
                        Condition("subject.roles", Operator.CONTAINS, value = "ADMIN"),
                    )

                val adminContext = context(subject = Subject("u1", roles = setOf("ADMIN", "USER")))
                val userContext = context(subject = Subject("u1", roles = setOf("USER")))

                evaluator.evaluate(policy, adminContext).isAllowed shouldBe true
                evaluator.evaluate(policy, userContext).isAllowed shouldBe false
            }

            test("GREATER_THAN: 값이 크면 true") {
                val policy =
                    policyWithCondition(
                        Condition("resource.amount", Operator.GREATER_THAN, value = 1000),
                    )

                evaluator.evaluate(policy, contextWithResource("amount" to 1500)).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("amount" to 1000)).isAllowed shouldBe false
                evaluator.evaluate(policy, contextWithResource("amount" to 500)).isAllowed shouldBe false
            }

            test("LESS_THAN_OR_EQUAL: 값이 작거나 같으면 true") {
                val policy =
                    policyWithCondition(
                        Condition("resource.amount", Operator.LESS_THAN_OR_EQUAL, value = 1000000),
                    )

                evaluator.evaluate(policy, contextWithResource("amount" to 500000)).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("amount" to 1000000)).isAllowed shouldBe true
                evaluator.evaluate(policy, contextWithResource("amount" to 1500000)).isAllowed shouldBe false
            }
        }

        context("복합 조건 (AND)") {

            test("모든 조건이 충족되어야 규칙이 매칭된다") {
                val policy =
                    policy(
                        rules =
                            listOf(
                                rule(
                                    effect = Effect.ALLOW,
                                    conditions =
                                        listOf(
                                            Condition("subject.roles", Operator.CONTAINS, value = "MANAGER"),
                                            Condition("resource.amount", Operator.LESS_THAN_OR_EQUAL, value = 1000000),
                                            Condition("resource.status", Operator.EQUALS, value = "PENDING"),
                                        ),
                                ),
                            ),
                    )

                // 모든 조건 충족 → 허용
                val allowedContext =
                    context(
                        subject = Subject("u1", roles = setOf("MANAGER")),
                        resource =
                            Resource(
                                "settlement",
                                attributes = mapOf("amount" to 500000, "status" to "PENDING"),
                            ),
                    )
                evaluator.evaluate(policy, allowedContext).isAllowed shouldBe true

                // 금액 초과 → 거부
                val overAmountContext =
                    context(
                        subject = Subject("u1", roles = setOf("MANAGER")),
                        resource =
                            Resource(
                                "settlement",
                                attributes = mapOf("amount" to 2000000, "status" to "PENDING"),
                            ),
                    )
                evaluator.evaluate(policy, overAmountContext).isDenied shouldBe true

                // 역할 부족 → 거부
                val noRoleContext =
                    context(
                        subject = Subject("u1", roles = setOf("USER")),
                        resource =
                            Resource(
                                "settlement",
                                attributes = mapOf("amount" to 500000, "status" to "PENDING"),
                            ),
                    )
                evaluator.evaluate(policy, noRoleContext).isDenied shouldBe true
            }
        }
    }) {
    companion object {
        private var ruleCounter = 0

        fun policy(
            id: String = "test-policy",
            enabled: Boolean = true,
            rules: List<Rule>,
        ) = Policy(
            id = id,
            name = "Test Policy",
            resource = "test",
            action = "test",
            rules = rules,
            enabled = enabled,
        )

        fun rule(
            id: String = "rule-${++ruleCounter}",
            effect: Effect,
            conditions: List<Condition>,
        ) = Rule(
            id = id,
            name = "Test Rule $id",
            effect = effect,
            conditions = conditions,
        )

        fun context(
            subject: Subject = Subject("default-user"),
            resource: Resource = Resource("test"),
        ) = PolicyContext(
            subject = subject,
            resource = resource,
            action = "test",
        )

        fun policyWithCondition(condition: Condition) =
            policy(
                rules =
                    listOf(
                        rule(
                            effect = Effect.ALLOW,
                            conditions = listOf(condition),
                        ),
                    ),
            )

        fun contextWithResource(vararg attributes: Pair<String, Any>) =
            context(
                resource = Resource("test", attributes = mapOf(*attributes)),
            )
    }
}
