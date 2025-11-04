package com.nextmall.user.application.usecase

import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    @Transactional
    fun register(
        email: String,
        password: String,
        nickname: String,
    ): User {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateEmailException(email)
        }

        val user =
            User(
                email = email,
                password = passwordEncoder.encode(password),
                nickname = nickname,
            )

        return userRepository.save(user)
    }
}
