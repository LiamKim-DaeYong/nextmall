package com.nextmall.user.port.input

import com.nextmall.user.application.command.create.CreateUserCommandParam
import com.nextmall.user.application.command.create.CreateUserResult

interface CreateUserCommand {
    fun create(param: CreateUserCommandParam): CreateUserResult
}
