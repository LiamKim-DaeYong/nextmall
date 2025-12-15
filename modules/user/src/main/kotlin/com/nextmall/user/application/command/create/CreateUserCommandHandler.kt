package com.nextmall.user.application.command.create

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.domain.entity.User
import com.nextmall.user.domain.model.UserStatus
import com.nextmall.user.port.input.CreateUserCommand
import com.nextmall.user.port.output.UserCommandPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUserCommandHandler(
    private val userCommandPort: UserCommandPort,
    private val idGenerator: IdGenerator,
) : CreateUserCommand {
    @Transactional
    override fun create(
        param: CreateUserCommandParam,
    ): CreateUserResult {
        val user =
            User(
                id = idGenerator.generate(),
                nickname = param.nickname,
                email = param.email,
                status = UserStatus.PENDING,
            )

        val saved = userCommandPort.save(user)
        return CreateUserResult(saved)
    }
}
