package com.nextmall.user.domain.exception

class UserNotFoundException(
    message: String = "User not found",
) : RuntimeException(message)
