package com.nextmall.bffservice.presentation.controller

import com.nextmall.bff.application.user.query.GetUserFacade
import com.nextmall.bffservice.presentation.response.user.UserViewResponse
import com.nextmall.bffservice.presentation.response.user.toResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserFacade: GetUserFacade,
) {
    @GetMapping("/{userId}")
    suspend fun getUser(
        @PathVariable userId: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
    ): ResponseEntity<UserViewResponse> {
        val result = getUserFacade.getUser(userId, authorization)

        return ResponseEntity
            .ok(result.toResponse())
    }
}
