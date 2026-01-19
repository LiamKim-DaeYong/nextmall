package com.nextmall.bff.presentation.controller

import com.nextmall.bff.application.auth.login.LoginFacade
import com.nextmall.bff.application.auth.token.TokenFacade
import com.nextmall.bff.presentation.request.login.LoginRequest
import com.nextmall.bff.presentation.request.login.toCommand
import com.nextmall.bff.presentation.request.token.LogoutRequest
import com.nextmall.bff.presentation.request.token.RefreshTokenRequest
import com.nextmall.bff.presentation.response.login.TokenResponse
import com.nextmall.bff.presentation.response.login.toResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginFacade: LoginFacade,
    private val tokenFacade: TokenFacade,
) {
    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val result = loginFacade.login(request.toCommand())

        return ResponseEntity
            .ok(result.toResponse())
    }

    @PostMapping("/logout")
    suspend fun logout(
        @Valid @RequestBody request: LogoutRequest,
    ): ResponseEntity<Unit> {
        tokenFacade.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/tokens/refresh")
    suspend fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<TokenResponse> =
        ResponseEntity.ok(
            tokenFacade
                .refresh(request.refreshToken)
                .toResponse(),
        )
}
