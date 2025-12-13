package com.nextmall.auth.application.command.login.strategy

import com.nextmall.auth.application.command.login.LoginCommandParam
import com.nextmall.auth.application.query.account.AuthUserAccountContext
import com.nextmall.auth.domain.model.AuthProvider

interface AuthLoginStrategy {
    fun supports(provider: AuthProvider): Boolean

    fun login(command: LoginCommandParam): AuthUserAccountContext
}
