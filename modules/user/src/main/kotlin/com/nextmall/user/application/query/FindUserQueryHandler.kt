package com.nextmall.user.application.query

import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.port.input.FindUserQuery
import com.nextmall.user.port.output.UserQueryPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FindUserQueryHandler(
    private val userQueryPort: UserQueryPort,
) : FindUserQuery {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun findById(id: Long): UserContext =
        userQueryPort.findById(id)
            ?: run {
                logger.debug("User not found. id={}", id)
                throw UserNotFoundException()
            }
}
