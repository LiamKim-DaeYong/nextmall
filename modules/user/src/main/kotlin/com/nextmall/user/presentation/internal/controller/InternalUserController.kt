package com.nextmall.user.presentation.internal.controller

import com.nextmall.user.application.command.create.CreateUserCommandParam
import com.nextmall.user.port.input.ActivateUserCommand
import com.nextmall.user.port.input.CreateUserCommand
import com.nextmall.user.port.input.MarkSignupFailedCommand
import com.nextmall.user.presentation.internal.request.CreateUserRequest
import com.nextmall.user.presentation.internal.response.CreateUserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/api/v1/users")
class InternalUserController(
    private val createUserCommand: CreateUserCommand,
    private val activateUserCommand: ActivateUserCommand,
    private val markSignupFailedCommand: MarkSignupFailedCommand,
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateUserRequest,
    ): ResponseEntity<CreateUserResponse> {
        val result =
            createUserCommand.create(
                CreateUserCommandParam(
                    nickname = request.nickname,
                    email = request.email,
                ),
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateUserResponse(result.id))
    }

    @PostMapping("/{userId}/activate")
    fun activate(
        @PathVariable userId: Long,
    ): ResponseEntity<Unit> {
        activateUserCommand.activate(userId)

        return ResponseEntity
            .ok()
            .build()
    }

    @PostMapping("/{userId}/signup-failed")
    fun signupFailed(
        @PathVariable userId: Long,
    ): ResponseEntity<Unit> {
        markSignupFailedCommand.markFailed(userId)

        return ResponseEntity
            .ok()
            .build()
    }
}
