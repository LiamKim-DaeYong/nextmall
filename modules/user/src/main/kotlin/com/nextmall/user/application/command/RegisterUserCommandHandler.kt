package com.nextmall.user.application.command

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.application.command.dto.RegisterUserResult
import com.nextmall.user.application.port.output.PasswordHasher
import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.entity.User
import com.nextmall.user.application.port.output.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val idGenerator: IdGenerator,
) : RegisterUserCommand {
    @Transactional
    override fun register(
        email: String,
        password: String,
        nickname: String,
    ): RegisterUserResult {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateEmailException(email)
        }

        val user =
            User(
                id = idGenerator.generate(),
                email = email,
                password = passwordHasher.encode(password),
                nickname = nickname,
            )

        val saved = userRepository.save(user)
        return RegisterUserResult.from(saved)
    }
}
