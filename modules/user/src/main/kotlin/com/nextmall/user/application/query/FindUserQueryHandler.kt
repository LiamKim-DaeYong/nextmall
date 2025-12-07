package com.nextmall.user.application.query

import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.infrastructure.jooq.UserQueryRepositoryImpl
import org.springframework.stereotype.Service

@Service
class FindUserQueryHandler(
    private val userQueryRepository: UserQueryRepositoryImpl,
) : FindUserQuery {
    override fun findById(id: Long): UserView =
        userQueryRepository.findById(id)
            ?: throw NoSuchElementException("User not found: $id")

    override fun findByEmail(email: String): UserView =
        userQueryRepository.findByEmail(email)
            ?: throw NoSuchElementException("User not found. email=$email")
}
