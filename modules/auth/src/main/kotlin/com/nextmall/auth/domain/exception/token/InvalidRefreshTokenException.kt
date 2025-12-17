package com.nextmall.auth.domain.exception.token

import com.nextmall.auth.domain.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 유효하지 않거나 만료된 refresh token이 사용되었을 때 발생하는 예외
 */
class InvalidRefreshTokenException : BaseException(AuthErrorCode.INVALID_REFRESH_TOKEN)
