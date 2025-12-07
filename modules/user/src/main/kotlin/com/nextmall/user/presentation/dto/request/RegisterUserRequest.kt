package com.nextmall.user.presentation.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank @field:Size(min = 8, max = 72) val password: String,
    @field:NotBlank val nickname: String,
) {
    override fun toString(): String =
        "RegisterUserRequest(email='$email', password='***', nickname='$nickname')"
}
