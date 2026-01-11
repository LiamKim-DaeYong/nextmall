package com.nextmall.common.authorization.annotation

/**
 * 메서드 레벨 정책 기반 인가 어노테이션.
 *
 * 이 어노테이션이 붙은 메서드는 호출 전에 정책 평가를 수행한다.
 * 평가 결과가 DENY면 AccessDeniedException이 발생한다.
 *
 * 사용 예:
 * ```
 * @RequiresPolicy(resource = "order", action = "read")
 * fun getOrder(orderId: Long): OrderView
 *
 * @RequiresPolicy(resource = "user", action = "update", resourceIdParam = "userId")
 * fun updateUser(userId: Long, request: UpdateUserRequest): UserView
 * ```
 *
 * @property resource 리소스 타입 (예: "order", "product", "settlement")
 * @property action 액션 (예: "read", "create", "update", "delete", "approve")
 * @property resourceIdParam 리소스 ID를 추출할 파라미터 이름 (선택적)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPolicy(
    val resource: String,
    val action: String,
    val resourceIdParam: String = "",
)
