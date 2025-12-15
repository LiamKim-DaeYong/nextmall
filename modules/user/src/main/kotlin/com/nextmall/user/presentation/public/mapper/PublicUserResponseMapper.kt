package com.nextmall.user.presentation.public.mapper

import com.nextmall.user.application.query.UserContext
import com.nextmall.user.presentation.public.response.PublicUserResponse
import org.springframework.stereotype.Component

@Component
class PublicUserResponseMapper {
    fun toPublicUserResponse(view: UserContext) =
        PublicUserResponse(
            id = view.id,
            nickname = view.nickname,
        )
}
