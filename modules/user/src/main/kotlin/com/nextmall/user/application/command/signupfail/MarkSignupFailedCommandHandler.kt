package com.nextmall.user.application.command.signupfail

import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.port.input.MarkSignupFailedCommand
import com.nextmall.user.port.output.UserCommandPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkSignupFailedCommandHandler(
    private val userCommandPort: UserCommandPort,
) : MarkSignupFailedCommand {
    @Transactional
    override fun markFailed(userId: Long) {
        val user =
            userCommandPort.findById(userId)
                ?: throw UserNotFoundException()

        user.markSignupFailed()
        userCommandPort.save(user)
    }
}
