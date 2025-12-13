package com.nextmall.auth.port.input.account

import com.nextmall.auth.application.command.account.CreateAuthUserAccountCommandParam
import com.nextmall.auth.application.command.account.CreateAuthUserAccountResult

interface CreateAuthUserAccountCommand {
    fun create(param: CreateAuthUserAccountCommandParam): CreateAuthUserAccountResult
}
