package com.nextmall.bff.presentation.controller

import com.nextmall.bff.application.user.query.GetUserFacade
import com.nextmall.bff.presentation.response.user.UserViewResponse
import com.nextmall.bff.presentation.response.user.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserFacade: GetUserFacade,
) {
    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ): Mono<ResponseEntity<UserViewResponse>> =
        getUserFacade
            .getUser(userId)
            .map { result -> ResponseEntity.ok(result.toResponse()) }
}
