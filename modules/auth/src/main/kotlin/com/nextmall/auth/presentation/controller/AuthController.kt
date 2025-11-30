package com.nextmall.auth.presentation.controller

import com.nextmall.auth.application.usecase.LoginUseCase
import com.nextmall.auth.application.usecase.LogoutUseCase
import com.nextmall.auth.application.usecase.RefreshTokenUseCase
import com.nextmall.auth.presentation.dto.LoginRequest
import com.nextmall.auth.presentation.dto.RefreshTokenRequest
import com.nextmall.auth.presentation.dto.TokenResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val tokenResponse =
            loginUseCase.login(
                email = request.email,
                password = request.password,
            )

        return ResponseEntity
            .ok(tokenResponse)
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<TokenResponse> {
        val response = refreshTokenUseCase.refresh(request.refreshToken)

        return ResponseEntity
            .ok(response)
    }

    @PostMapping("/logout")
    fun logout(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<Unit> {
        logoutUseCase.logout(request.refreshToken)

        return ResponseEntity
            .ok()
            .build()
    }
}
