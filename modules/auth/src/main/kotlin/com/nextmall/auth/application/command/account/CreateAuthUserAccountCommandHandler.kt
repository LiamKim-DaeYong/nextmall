package com.nextmall.auth.application.command.account

import com.nextmall.auth.domain.entity.AuthUserAccount
import com.nextmall.auth.port.input.account.CreateAuthUserAccountCommand
import com.nextmall.auth.port.output.account.AuthUserAccountCommandPort
import com.nextmall.common.identifier.IdGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateAuthUserAccountCommandHandler(
    private val idGenerator: IdGenerator,
    private val passwordEncoder: PasswordEncoder,
    private val authUserAccountCommandPort: AuthUserAccountCommandPort,
) : CreateAuthUserAccountCommand {
    @Transactional
    override fun create(
        param: CreateAuthUserAccountCommandParam,
    ) {
        val authUserAccount =
            AuthUserAccount(
                id = idGenerator.generate(),
                userId = param.userId,
                provider = param.provider,
                providerAccountId = param.providerAccountId,
                passwordHash = param.password?.let { passwordEncoder.encode(it) },
            )

        authUserAccountCommandPort.save(authUserAccount)
    }
}
