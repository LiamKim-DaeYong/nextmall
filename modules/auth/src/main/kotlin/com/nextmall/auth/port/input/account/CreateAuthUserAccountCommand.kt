package com.nextmall.auth.port.input.account

import com.nextmall.auth.application.command.account.CreateAuthUserAccountCommandParam

interface CreateAuthUserAccountCommand {
    fun create(param: CreateAuthUserAccountCommandParam)
}
