package com.nextmall.user.presentation.mapper

import com.nextmall.user.domain.model.User
import com.nextmall.user.presentation.dto.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toResponse(user: User): UserResponse =
        UserResponse(
            id = user.id!!,
            email = user.email,
            nickname = user.nickname,
            role = user.role.name
        )
}
