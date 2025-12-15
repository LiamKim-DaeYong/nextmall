package com.nextmall.user.presentation.public.controller

import com.nextmall.user.port.input.FindUserQuery
import com.nextmall.user.presentation.public.mapper.PublicUserResponseMapper
import com.nextmall.user.presentation.public.response.PublicUserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class PublicUserController(
    private val findUserQuery: FindUserQuery,
    private val mapper: PublicUserResponseMapper,
) {
    @GetMapping("/{id}")
    fun getPublicUser(
        @PathVariable id: Long,
    ): ResponseEntity<PublicUserResponse> {
        val result = findUserQuery.findById(id)

        return ResponseEntity
            .ok()
            .body(mapper.toPublicUserResponse(result))
    }
}
