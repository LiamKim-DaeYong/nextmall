package com.nextmall.user.presentation.mapper

import com.nextmall.user.application.command.dto.RegisterUserResult
import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.presentation.dto.response.PublicUserResponse
import com.nextmall.user.presentation.dto.response.RegisterUserResponse
import org.springframework.stereotype.Component

@Component
class UserResponseMapper {
    fun toRegisterUserResponse(result: RegisterUserResult) =
        RegisterUserResponse(
            id = result.id,
            email = result.email,
            nickname = result.nickname,
            createdAt = result.createdAt,
        )

    fun toPublicUserResponse(view: UserView) =
        PublicUserResponse(
            id = view.id,
            nickname = view.nickname,
        )
}
