package com.nextmall.auth.port.output.account

import com.nextmall.auth.domain.entity.AuthUserAccount

interface AuthUserAccountCommandPort {
    fun saveAndFlush(authUserAccount: AuthUserAccount): AuthUserAccount
}
