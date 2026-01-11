package com.nextmall.common.policy.factory

import com.nextmall.common.policy.model.Effect
import com.nextmall.common.policy.model.Operator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PolicyDslBuilderTest :
    FunSpec({

        context("policy DSL") {

            test("기본 정책을 생성한다") {
                val result =
                    policy("user", "read") {
                        name = "사용자 조회 정책"
                        description = "테스트 정책"

                        allow("관리자 조회") {
                            condition("subject.roles", Operator.CONTAINS, "ADMIN")
                        }
                    }

                result.id shouldBe "user:read"
                result.name shouldBe "사용자 조회 정책"
                result.description shouldBe "테스트 정책"
                result.resource shouldBe "user"
                result.action shouldBe "read"
                result.rules shouldHaveSize 1
                result.enabled shouldBe true
            }

            test("여러 규칙을 가진 정책을 생성한다") {
                val result =
                    policy("user", "read") {
                        name = "복합 정책"

                        deny("차단된 사용자") {
                            condition("subject.status", Operator.EQUALS, "BLOCKED")
                        }

                        allow("관리자") {
                            condition("subject.roles", Operator.CONTAINS, "ADMIN")
                        }

                        allow("본인 조회") {
                            conditionRef("subject.userId", Operator.EQUALS, "resource.id")
                        }
                    }

                result.rules shouldHaveSize 3
                result.denyRules shouldHaveSize 1
                result.allowRules shouldHaveSize 2
            }

            test("규칙의 priority는 선언 순서대로 증가한다") {
                val result =
                    policy("user", "read") {
                        allow("첫번째") {
                            condition("subject.roles", Operator.CONTAINS, "A")
                        }
                        allow("두번째") {
                            condition("subject.roles", Operator.CONTAINS, "B")
                        }
                        allow("세번째") {
                            condition("subject.roles", Operator.CONTAINS, "C")
                        }
                    }

                result.rules[0].priority shouldBe 0
                result.rules[1].priority shouldBe 1
                result.rules[2].priority shouldBe 2
            }

            test("규칙 ID는 resource:action:rule-N 형식이다") {
                val result =
                    policy("order", "approve") {
                        allow("첫번째") {
                            condition("subject.roles", Operator.CONTAINS, "A")
                        }
                        allow("두번째") {
                            condition("subject.roles", Operator.CONTAINS, "B")
                        }
                    }

                result.rules[0].id shouldBe "order:approve:rule-0"
                result.rules[1].id shouldBe "order:approve:rule-1"
            }

            test("조건에 고정값을 사용할 수 있다") {
                val result =
                    policy("user", "read") {
                        allow("조건 테스트") {
                            condition("subject.roles", Operator.CONTAINS, "ADMIN")
                            condition("resource.status", Operator.EQUALS, "ACTIVE")
                        }
                    }

                val conditions = result.rules[0].conditions
                conditions shouldHaveSize 2
                conditions[0].value shouldBe "ADMIN"
                conditions[0].valueRef shouldBe null
                conditions[1].value shouldBe "ACTIVE"
            }

            test("조건에 참조값을 사용할 수 있다") {
                val result =
                    policy("user", "read") {
                        allow("본인 조회") {
                            conditionRef("subject.userId", Operator.EQUALS, "resource.ownerId")
                        }
                    }

                val condition = result.rules[0].conditions[0]
                condition.value shouldBe null
                condition.valueRef shouldBe "resource.ownerId"
            }

            test("DENY 규칙을 생성할 수 있다") {
                val result =
                    policy("user", "read") {
                        deny("차단") {
                            condition("subject.status", Operator.EQUALS, "BLOCKED")
                        }
                    }

                result.rules[0].effect shouldBe Effect.DENY
            }
        }
    })
