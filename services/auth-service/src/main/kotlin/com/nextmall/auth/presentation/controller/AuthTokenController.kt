package com.nextmall.auth.presentation.controller

import com.nextmall.auth.application.AuthTokenService
import com.nextmall.auth.presentation.request.token.IssueTokenRequest
import com.nextmall.auth.presentation.request.token.LoginRequest
import com.nextmall.auth.presentation.request.token.RefreshTokenRequest
import com.nextmall.auth.presentation.response.token.TokenResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/tokens")
class AuthTokenController(
    private val authTokenService: AuthTokenService,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val result =
            authTokenService.login(
                provider = request.provider,
                identifier = request.principal,
                credential = request.credential,
            )

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            ),
        )
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<TokenResponse> {
        val result =
            authTokenService.refresh(request.refreshToken)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            ),
        )
    }

    @PostMapping("/logout")
    fun logout(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<Unit> {
        authTokenService.revoke(request.refreshToken)
        return ResponseEntity.noContent().build()
    }

    /**
     * 회원가입 직후 토큰 발급용 (내부 호출)
     */
    @PostMapping("/issue")
    fun issue(
        @Valid @RequestBody request: IssueTokenRequest,
    ): ResponseEntity<TokenResponse> {
        val result =
            authTokenService.issueForAuthAccount(request.authAccountId)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            ),
        )
    }
}
