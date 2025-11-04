package com.nextmall.user.presentation.controller

import com.nextmall.user.application.usecase.FindUserUseCase
import com.nextmall.user.application.usecase.RegisterUserUseCase
import com.nextmall.user.presentation.dto.RegisterUserRequest
import com.nextmall.user.presentation.dto.RegisterUserResponse
import com.nextmall.user.presentation.dto.UserResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val findUserUseCase: FindUserUseCase,
) {
    @PostMapping
    fun register(
        @RequestBody @Validated req: RegisterUserRequest,
    ): RegisterUserResponse =
        registerUserUseCase.register(
            email = req.email,
            password = req.password,
            nickname = req.nickname,
        )

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
    ): UserResponse = findUserUseCase.findById(id)
}
