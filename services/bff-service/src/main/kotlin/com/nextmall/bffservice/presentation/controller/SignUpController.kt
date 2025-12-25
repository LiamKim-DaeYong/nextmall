package com.nextmall.bffservice.presentation.controller

import com.nextmall.bff.application.signup.SignUpFacade
import com.nextmall.bffservice.presentation.request.signup.LocalSignUpRequest
import com.nextmall.bffservice.presentation.request.signup.SocialSignUpRequest
import com.nextmall.bffservice.presentation.request.signup.toCommand
import com.nextmall.bffservice.presentation.response.signup.SignUpResponse
import com.nextmall.bffservice.presentation.response.signup.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sign-up")
class SignUpController(
    private val signUpFacade: SignUpFacade,
) {
    @PostMapping("/local")
    suspend fun local(
        @Valid @RequestBody request: LocalSignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val result = signUpFacade.signUp(request.toCommand())

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result.toResponse())
    }

    @PostMapping("/social")
    fun social(@RequestBody request: SocialSignUpRequest): ResponseEntity<SignUpResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_IMPLEMENTED)
            .build()
}
