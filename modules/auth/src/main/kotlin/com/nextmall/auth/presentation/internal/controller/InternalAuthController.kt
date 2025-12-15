package com.nextmall.auth.presentation.internal.controller

import com.nextmall.auth.application.command.account.CreateAuthUserAccountCommandParam
import com.nextmall.auth.port.input.account.CreateAuthUserAccountCommand
import com.nextmall.auth.port.input.token.IssueTokenCommand
import com.nextmall.auth.presentation.internal.request.CreateAuthUserAccountRequest
import com.nextmall.auth.presentation.internal.request.IssueTokenRequest
import com.nextmall.auth.presentation.public.mapper.AuthResponseMapper
import com.nextmall.auth.presentation.public.response.TokenResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/api/v1/auth")
class InternalAuthController(
    private val createAuthUserAccountCommand: CreateAuthUserAccountCommand,
    private val issueTokenCommand: IssueTokenCommand,
    private val mapper: AuthResponseMapper,
) {
    @PostMapping("/accounts")
    suspend fun create(
        @Valid @RequestBody request: CreateAuthUserAccountRequest,
    ): ResponseEntity<Unit> {
        createAuthUserAccountCommand.create(
            CreateAuthUserAccountCommandParam(
                userId = request.userId,
                provider = request.provider,
                providerAccountId = request.providerAccountId,
                password = request.password,
            ),
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build()
    }

    @PostMapping("/tokens")
    suspend fun issueToken(
        @Valid @RequestBody request: IssueTokenRequest,
    ): ResponseEntity<TokenResponse> {
        val result = issueTokenCommand.issue(request.userId)

        return ResponseEntity
            .ok(mapper.toTokenResponse(result))
    }
}
