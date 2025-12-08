package com.nextmall.user.application.query

import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.application.port.output.UserQueryRepository
import org.springframework.stereotype.Service

@Service
class FindUserQueryHandler(
    private val userQueryRepository: UserQueryRepository,
) : FindUserQuery {
    override fun findById(id: Long): UserView =
        userQueryRepository.findById(id)
            ?: throw UserNotFoundException("User not found: $id")

    override fun findByEmail(email: String): UserView =
        userQueryRepository.findByEmail(email)
            ?: throw UserNotFoundException("User not found. email=$email")
}
