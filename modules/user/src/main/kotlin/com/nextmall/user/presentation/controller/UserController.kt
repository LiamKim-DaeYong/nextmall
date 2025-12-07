package com.nextmall.user.presentation.controller

import com.nextmall.user.application.query.FindUserQuery
import com.nextmall.user.application.usecase.RegisterUserUseCase
import com.nextmall.user.presentation.dto.response.PublicUserResponse
import com.nextmall.user.presentation.dto.request.RegisterUserRequest
import com.nextmall.user.presentation.dto.response.RegisterUserResponse
import com.nextmall.user.presentation.mapper.UserViewMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val findUserQuery: FindUserQuery,
    private val userViewMapper: UserViewMapper,
) {
    @PostMapping
    fun register(
        @RequestBody @Validated req: RegisterUserRequest,
    ): ResponseEntity<RegisterUserResponse> {
        val registered =
            registerUserUseCase.register(
                email = req.email,
                password = req.password,
                nickname = req.nickname,
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(registered)
    }

    @GetMapping("/{id}")
    fun getPublicUser(@PathVariable id: Long): PublicUserResponse =
        userViewMapper.toPublicUserResponse(findUserQuery.findById(id))
}
