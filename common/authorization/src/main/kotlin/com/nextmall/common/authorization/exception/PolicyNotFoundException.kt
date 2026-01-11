package com.nextmall.common.authorization.exception

import com.nextmall.common.exception.base.BaseException

/**
 * 요청한 리소스와 액션에 대한 정책을 찾을 수 없을 때 발생하는 예외.
 */
class PolicyNotFoundException(
    val resource: String,
    val action: String,
) : BaseException(
        errorCode = AuthorizationErrorCode.POLICY_NOT_FOUND,
        expected = true,
    ) {
    override val message: String
        get() = "Policy not found for resource '$resource' and action '$action'"
}
