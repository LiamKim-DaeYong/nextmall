package com.nextmall.bff.signup.application.command

import com.nextmall.auth.application.command.account.CreateAuthUserAccountCommandParam
import com.nextmall.auth.port.input.account.CreateAuthUserAccountCommand
import com.nextmall.bff.signup.application.result.SignUpResult
import com.nextmall.user.application.command.create.CreateUserCommandParam
import com.nextmall.user.port.input.CreateUserCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpCommandHandler(
    private val createUserCommand: CreateUserCommand,
    private val createAuthUserAccountCommand: CreateAuthUserAccountCommand,
) {
    @Transactional
    fun handle(param: SignUpCommandParam): SignUpResult {
        val user =
            createUserCommand.create(
                CreateUserCommandParam(
                    nickname = param.nickname,
                    email = null,
                ),
            )

        val authUserAccount =
            createAuthUserAccountCommand.create(
                CreateAuthUserAccountCommandParam(
                    userId = user.id,
                    provider = param.provider,
                    providerAccountId = param.providerAccountId,
                    password = param.password,
                ),
            )

        return SignUpResult(
            userId = user.id,
            authAccountId = authUserAccount.id,
        )
    }
}
