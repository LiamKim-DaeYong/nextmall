package com.nextmall.user.application.usecase

import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FindUserUseCase(
    private val userRepository: UserRepository,
) {
    fun findById(id: Long): User = userRepository.findById(id).orElseThrow { NoSuchElementException("User($id) not found") }
}
