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
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginFacade: LoginFacade,
    private val tokenFacade: TokenFacade,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): Mono<ResponseEntity<TokenResponse>> =
        loginFacade
            .login(request.toCommand())
            .map { result -> ResponseEntity.ok(result.toResponse()) }

    @PostMapping("/logout")
    fun logout(
        @Valid @RequestBody request: LogoutRequest,
    ): Mono<ResponseEntity<Unit>> =
        tokenFacade
            .logout(request.refreshToken)
            .thenReturn(ResponseEntity.noContent().build())

    @PostMapping("/tokens/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): Mono<ResponseEntity<TokenResponse>> =
        tokenFacade
            .refresh(request.refreshToken)
            .map { result -> ResponseEntity.ok(result.toResponse()) }
}
