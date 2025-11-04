package com.nextmall.user.application.usecase

import com.nextmall.user.domain.repository.UserRepository
import com.nextmall.user.presentation.dto.UserResponse
import com.nextmall.user.presentation.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FindUserUseCase(
    private val userRepository: UserRepository,
    private val mapper: UserMapper,
) {
    @Transactional(readOnly = true)
    fun findById(id: Long): UserResponse {
        val user =
            userRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("User($id) not found") }

        return mapper.toResponse(user)
    }
}
