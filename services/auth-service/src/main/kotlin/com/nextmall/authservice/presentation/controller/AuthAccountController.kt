package com.nextmall.authservice.presentation.controller

import com.nextmall.auth.application.AuthAccountService
import com.nextmall.authservice.presentation.request.CreateAuthAccountRequest
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
    suspend fun createAccount(
        @RequestBody request: CreateAuthAccountRequest,
    ) {
        authAccountService.createAccount(
            userId = request.userId,
            provider = request.provider,
            providerAccountId = request.providerAccountId,
            password = request.password,
        )
    }
}
