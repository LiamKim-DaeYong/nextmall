package com.nextmall.auth.domain.exception.login

import com.nextmall.auth.domain.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 로그인 시도 횟수 제한을 초과했을 때 발생하는 예외
 */
class TooManyLoginAttemptsException : BaseException(AuthErrorCode.TOO_MANY_LOGIN_ATTEMPTS)
