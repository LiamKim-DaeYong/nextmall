package com.nextmall.user.application.command.activate

import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.port.input.ActivateUserCommand
import com.nextmall.user.port.output.UserCommandPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivateUserCommandHandler(
    private val userCommandPort: UserCommandPort,
) : ActivateUserCommand {
    @Transactional
    override fun activate(userId: Long) {
        val user =
            userCommandPort.findById(userId)
                ?: throw UserNotFoundException()

        user.activate()
        userCommandPort.save(user)
    }
}
