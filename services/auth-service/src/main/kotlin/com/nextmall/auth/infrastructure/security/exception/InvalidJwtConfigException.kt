package com.nextmall.auth.infrastructure.security.exception

import com.nextmall.auth.application.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * JWT 관련 서버 설정이 올바르지 않을 때 발생하는 예외.
 */
class InvalidJwtConfigException : BaseException(AuthErrorCode.INVALID_JWT_CONFIG)
