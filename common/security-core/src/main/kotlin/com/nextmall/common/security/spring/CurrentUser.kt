package com.nextmall.common.security.spring

/**
 * 현재 인증된 사용자 정보를 컨트롤러 파라미터로 주입받기 위한 어노테이션.
 *
 * Passport Token의 claims에서 추출한 AuthenticatedPrincipal을 주입한다.
 *
 * @see CurrentUserArgumentResolver
 *
 * 사용 예시:
 * ```
 * @GetMapping("/me")
 * fun getMe(@CurrentUser user: AuthenticatedPrincipal): UserResponse
 *
 * // 인증되지 않은 요청도 허용 (permitAll 엔드포인트)
 * @GetMapping("/products")
 * fun getProducts(@CurrentUser(required = false) user: AuthenticatedPrincipal?): List<Product>
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CurrentUser(
    /**
     * true: 인증 필수 (미인증 시 예외 발생)
     * false: 인증 선택 (미인증 시 null 반환, 파라미터는 nullable이어야 함)
     */
    val required: Boolean = true,
)
