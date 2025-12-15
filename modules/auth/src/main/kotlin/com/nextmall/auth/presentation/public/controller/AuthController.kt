package com.nextmall.auth.presentation.public.controller

import com.nextmall.auth.application.command.login.LoginCommandParam
import com.nextmall.auth.application.command.logout.LogoutCommandHandler
import com.nextmall.auth.port.input.login.LoginCommand
import com.nextmall.auth.port.input.token.RefreshTokenCommand
import com.nextmall.auth.presentation.public.mapper.AuthResponseMapper
import com.nextmall.auth.presentation.public.request.LoginRequest
import com.nextmall.auth.presentation.public.request.RefreshTokenRequest
import com.nextmall.auth.presentation.public.response.TokenResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginCommand: LoginCommand,
    private val refreshTokenCommand: RefreshTokenCommand,
    private val logoutCommandHandler: LogoutCommandHandler,
    private val mapper: AuthResponseMapper,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val param =
            LoginCommandParam(
                provider = request.provider,
                principal = request.principal,
                credential = request.credential,
            )

        val result = loginCommand.login(param)

        return ResponseEntity
            .ok(mapper.toTokenResponse(result))
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<TokenResponse> {
        val result = refreshTokenCommand.refreshToken(request.refreshToken)

        return ResponseEntity
            .ok(mapper.toTokenResponse(result))
    }

    @PostMapping("/logout")
    fun logout(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<Unit> {
        logoutCommandHandler.logout(request.refreshToken)

        return ResponseEntity
            .ok()
            .build()
    }
}
