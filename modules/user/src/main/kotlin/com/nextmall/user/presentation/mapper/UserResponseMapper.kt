package com.nextmall.user.presentation.mapper

import com.nextmall.user.application.query.UserContext
import com.nextmall.user.presentation.response.PublicUserResponse
import org.springframework.stereotype.Component

@Component
class UserResponseMapper {
    fun toPublicUserResponse(view: UserContext) =
        PublicUserResponse(
            id = view.id,
            nickname = view.nickname,
        )
}
