package com.nextmall.user.presentation.mapper

import com.nextmall.user.domain.model.User
import com.nextmall.user.presentation.dto.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper {
    /**
     * Converts a persisted User entity to UserResponse.
     * @throws IllegalArgumentException if user.id is null (non-persisted entity)
     */
    fun toResponse(user: User): UserResponse =
        UserResponse(
            id = requireNotNull(user.id) { "User must be persisted before mapping to response" },
            email = user.email,
            nickname = user.nickname,
            role = user.role.name,
        )
}
