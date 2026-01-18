package com.nextmall.auth.application.exception

import com.nextmall.auth.application.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 잘못된 인증 정보로 인해 로그인이 실패했을 때 발생하는 예외
 */
class InvalidLoginException : BaseException(AuthErrorCode.INVALID_CREDENTIAL)
