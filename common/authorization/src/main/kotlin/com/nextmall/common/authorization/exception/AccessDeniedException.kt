package com.nextmall.common.authorization.exception

import com.nextmall.common.exception.base.BaseException

/**
 * 인가 실패 시 발생하는 예외.
 *
 * 정책 평가 결과가 DENY일 때 발생한다.
 */
class AccessDeniedException(
    val resource: String,
    val action: String,
    val reason: String? = null,
) : BaseException(
        errorCode = AuthorizationErrorCode.ACCESS_DENIED,
        expected = true,
    ) {
    override val message: String
        get() =
            buildString {
                append("Access denied for resource '$resource' and action '$action'")
                reason?.let { append(": $it") }
            }
}
