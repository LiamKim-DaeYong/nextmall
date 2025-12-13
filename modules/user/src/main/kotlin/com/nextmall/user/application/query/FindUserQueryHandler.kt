package com.nextmall.user.application.query

import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.port.input.FindUserQuery
import com.nextmall.user.port.output.UserQueryPort
import org.springframework.stereotype.Service

@Service
class FindUserQueryHandler(
    private val userQueryPort: UserQueryPort,
) : FindUserQuery {
    override fun findById(id: Long): UserContext =
        userQueryPort.findById(id)
            ?: throw UserNotFoundException("User not found: $id")
}
