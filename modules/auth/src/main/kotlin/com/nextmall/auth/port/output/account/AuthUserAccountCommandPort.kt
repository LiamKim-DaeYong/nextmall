package com.nextmall.auth.port.output.account

import com.nextmall.auth.domain.entity.AuthUserAccount

interface AuthUserAccountCommandPort {
    fun save(authUserAccount: AuthUserAccount): AuthUserAccount
}
