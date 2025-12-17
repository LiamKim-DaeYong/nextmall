package com.nextmall.auth.domain.exception.login

import com.nextmall.auth.domain.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 잘못된 인증 정보로 인해 로그인이 실패했을 때 발생하는 예외
 */
class InvalidLoginException : BaseException(AuthErrorCode.INVALID_CREDENTIAL)
