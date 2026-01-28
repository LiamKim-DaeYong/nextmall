package com.nextmall.user.presentation.controller

import com.nextmall.common.authorization.annotation.RequiresPolicy
import com.nextmall.user.application.UserService
import com.nextmall.user.presentation.request.CreateUserRequest
import com.nextmall.user.presentation.response.CreateUserResponse
import com.nextmall.user.presentation.response.UserViewResponse
import com.nextmall.user.presentation.response.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/{userId}")
    @RequiresPolicy(resource = "user", action = "read", resourceIdParam = "userId")
    fun getUser(
        @PathVariable userId: Long,
    ): ResponseEntity<UserViewResponse> {
        val result = userService.getUser(userId)
        return ResponseEntity
            .ok(result.toResponse())
    }

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateUserRequest,
    ): ResponseEntity<CreateUserResponse> {
        val result =
            userService.create(
                nickname = request.nickname,
                email = request.email,
            )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateUserResponse(result.id))
    }

    @PostMapping("/{userId}/activate")
    fun activate(
        @PathVariable userId: Long,
    ): ResponseEntity<Unit> {
        userService.activate(userId)
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{userId}/signup-failed")
    fun signupFailed(
        @PathVariable userId: Long,
    ): ResponseEntity<Unit> {
        userService.markFailed(userId)
        return ResponseEntity
            .noContent()
            .build()
    }
}
