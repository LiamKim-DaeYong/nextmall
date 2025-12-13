package com.nextmall.user.presentation.controller

import com.nextmall.user.port.input.FindUserQuery
import com.nextmall.user.presentation.mapper.UserResponseMapper
import com.nextmall.user.presentation.response.PublicUserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val findUserQuery: FindUserQuery,
    private val mapper: UserResponseMapper,
) {
    @GetMapping("/{id}")
    fun getPublicUser(@PathVariable id: Long): PublicUserResponse =
        mapper.toPublicUserResponse(findUserQuery.findById(id))
}
