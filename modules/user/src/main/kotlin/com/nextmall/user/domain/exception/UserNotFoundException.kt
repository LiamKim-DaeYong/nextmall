package com.nextmall.user.domain.exception

import com.nextmall.common.exception.base.BaseException

class UserNotFoundException : BaseException(UserErrorCode.USER_NOT_FOUND)
