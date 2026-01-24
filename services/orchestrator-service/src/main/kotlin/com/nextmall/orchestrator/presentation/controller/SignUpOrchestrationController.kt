package com.nextmall.orchestrator.presentation.controller

import com.nextmall.orchestrator.application.signup.SignUpFacade
import com.nextmall.orchestrator.presentation.request.signup.SignUpOrchestrationRequest
import com.nextmall.orchestrator.presentation.request.signup.toCommand
import com.nextmall.orchestrator.presentation.response.signup.SignUpOrchestrationResponse
import com.nextmall.orchestrator.presentation.response.signup.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestrations/sign-up")
class SignUpOrchestrationController(
    private val signUpFacade: SignUpFacade,
) {
    @PostMapping
    fun signUp(
        @Valid @RequestBody request: SignUpOrchestrationRequest,
    ): Mono<ResponseEntity<SignUpOrchestrationResponse>> =
        signUpFacade
            .signUp(request.toCommand())
            .map { result ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result.toResponse())
            }
}
