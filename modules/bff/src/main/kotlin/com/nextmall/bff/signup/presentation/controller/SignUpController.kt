package com.nextmall.bff.signup.presentation.controller

import com.nextmall.bff.integration.auth.AuthProvider
import com.nextmall.bff.signup.application.command.SignUpCommandHandler
import com.nextmall.bff.signup.application.command.SignUpCommandParam
import com.nextmall.bff.signup.presentation.request.LocalSignUpRequest
import com.nextmall.bff.signup.presentation.request.SocialSignUpRequest
import com.nextmall.bff.signup.presentation.response.SignUpResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sign-up")
class SignUpController(
    private val signUpCommandHandler: SignUpCommandHandler,
) {
    @PostMapping("/local")
    suspend fun local(
        @Valid @RequestBody request: LocalSignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val param =
            SignUpCommandParam(
                provider = AuthProvider.LOCAL,
                providerAccountId = request.email.trim().lowercase(),
                password = request.password,
                nickname = request.nickname,
            )

        val result = signUpCommandHandler.handle(param)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                SignUpResponse(
                    userId = result.userId,
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                ),
            )
    }

    @PostMapping("/social")
    fun social(@RequestBody request: SocialSignUpRequest): ResponseEntity<SignUpResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_IMPLEMENTED)
            .build()
}
