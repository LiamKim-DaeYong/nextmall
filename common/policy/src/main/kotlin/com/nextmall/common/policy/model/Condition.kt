package com.nextmall.common.policy.model

/**
 * 정책 조건.
 * field의 값을 operator로 value 또는 valueRef와 비교한다.
 *
 * @property field 비교할 필드 경로 (예: "subject.roles", "resource.amount")
 * @property operator 비교 연산자
 * @property value 고정 비교 값 (valueRef와 둘 중 하나만 사용)
 * @property valueRef 동적 참조 경로 (예: "subject.assignedSellers")
 */
data class Condition(
    val field: String,
    val operator: Operator,
    val value: Any? = null,
    val valueRef: String? = null,
) {
    init {
        require(value != null || valueRef != null) {
            "value 또는 valueRef 중 하나는 필수입니다"
        }
        require(!(value != null && valueRef != null)) {
            "value와 valueRef는 동시에 사용할 수 없습니다"
        }
    }
}
