package com.nextmall.user.application.usecase

import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    fun register(email: String, password: String, nickname: String) {
        require(!userRepository.existsByEmail(email)) { "Email already exists" }
        val user = User(
            email = email,
            password = passwordEncoder.encode(password),
            nickname = nickname
        )
        userRepository.save(user)
    }
}
