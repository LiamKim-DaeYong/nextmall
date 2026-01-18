package com.nextmall.auth.application.exception

import com.nextmall.auth.application.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 지원하지 않는 인증 제공자가 요청되었을 때 발생하는 예외
 */
class UnsupportedProviderException : BaseException(AuthErrorCode.UNSUPPORTED_PROVIDER)
