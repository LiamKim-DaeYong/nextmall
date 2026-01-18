package com.nextmall.user.domain.exception

import com.nextmall.common.exception.base.BaseException
import com.nextmall.user.application.exception.UserErrorCode

class UserNotFoundException : BaseException(UserErrorCode.USER_NOT_FOUND)
