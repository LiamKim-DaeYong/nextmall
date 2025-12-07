package com.nextmall.user.presentation.mapper

import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.presentation.dto.response.PublicUserResponse
import com.nextmall.user.presentation.dto.response.UserProfileResponse
import org.springframework.stereotype.Component

@Component
class UserViewMapper {
    fun toUserProfileResponse(v: UserView) =
        UserProfileResponse(
            id = v.id,
            email = v.email,
            nickname = v.nickname,
            role = v.role,
            provider = v.provider,
            createdAt = v.createdAt.toString(),
        )

    fun toPublicUserResponse(v: UserView) =
        PublicUserResponse(
            id = v.id,
            nickname = v.nickname,
        )
}
