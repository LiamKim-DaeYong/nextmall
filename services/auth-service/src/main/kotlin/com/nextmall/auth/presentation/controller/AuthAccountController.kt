package com.nextmall.auth.presentation.controller

import com.nextmall.auth.application.AuthAccountService
import com.nextmall.auth.presentation.request.account.CreateAuthAccountRequest
import com.nextmall.auth.presentation.response.account.CreateAuthAccountResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/accounts")
class AuthAccountController(
    private val authAccountService: AuthAccountService,
) {
    @PostMapping
    fun createAccount(
        @Valid @RequestBody request: CreateAuthAccountRequest,
    ): ResponseEntity<CreateAuthAccountResponse> {
        val authAccountId =
            authAccountService.createAccount(
                userId = request.userId,
                provider = request.provider,
                providerAccountId = request.providerAccountId,
                password = request.password,
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateAuthAccountResponse(authAccountId))
    }
}
