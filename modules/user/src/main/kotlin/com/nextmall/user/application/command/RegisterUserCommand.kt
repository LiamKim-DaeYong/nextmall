package com.nextmall.user.application.command

import com.nextmall.user.application.command.dto.RegisterUserResult

interface RegisterUserCommand {
    fun register(
        email: String,
        password: String,
        nickname: String,
    ): RegisterUserResult
}
