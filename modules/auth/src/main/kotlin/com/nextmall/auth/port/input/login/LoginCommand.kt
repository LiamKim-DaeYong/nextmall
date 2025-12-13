package com.nextmall.auth.port.input.login

import com.nextmall.auth.application.command.login.LoginCommandParam
import com.nextmall.auth.application.command.token.TokenResult

interface LoginCommand {
    fun login(param: LoginCommandParam): TokenResult
}
