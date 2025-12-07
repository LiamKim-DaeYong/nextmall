package com.nextmall.user.application.usecase

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.application.port.output.PasswordHasher
import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import com.nextmall.user.presentation.dto.response.RegisterUserResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val idGenerator: IdGenerator,
) {
    @Transactional
    fun register(
        email: String,
        password: String,
        nickname: String,
    ): RegisterUserResponse {
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
        return RegisterUserResponse.from(saved)
    }
}
