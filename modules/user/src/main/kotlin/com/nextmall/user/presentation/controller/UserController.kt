package com.nextmall.user.presentation.controller

import com.nextmall.user.presentation.dto.RegisterUserRequest
import com.nextmall.user.presentation.dto.RegisterUserResponse
import com.nextmall.user.presentation.dto.UserResponse
import com.nextmall.user.presentation.mapper.UserMapper
import com.nextmall.user.application.usecase.FindUserUseCase
import com.nextmall.user.application.usecase.RegisterUserUseCase
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val findUserUseCase: FindUserUseCase,
    private val mapper: UserMapper
) {
    @PostMapping
    fun register(@RequestBody @Validated req: RegisterUserRequest): RegisterUserResponse {
        registerUserUseCase.register(req.email, req.password, req.nickname)
        return RegisterUserResponse(success = true)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserResponse =
        mapper.toResponse(findUserUseCase.findById(id))
}
