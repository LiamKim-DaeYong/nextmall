package com.nextmall.user.infrastructure.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.jooq.tables.references.USERS
import com.nextmall.user.application.query.dto.UserCredentials
import com.nextmall.user.domain.repository.UserCredentialsQueryRepository
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class UserCredentialsQueryRepositoryImpl(
    dsl: DSLContext
) : JooqRepository(dsl), UserCredentialsQueryRepository {

    private val fields = arrayOf(
        USERS.ID,
        USERS.PASSWORD,
        USERS.ROLE
    )

    override fun findById(id: Long): UserCredentials? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.ID.eq(id))
            .limit(1)
            .fetchOne { it.toUserCredentials() }

    override fun findByEmail(email: String): UserCredentials? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .limit(1)
            .fetchOne { it.toUserCredentials() }

    private fun Record.toUserCredentials(): UserCredentials =
        UserCredentials(
            id = getRequired(USERS.ID),
            password = get(USERS.PASSWORD),
            role = getRequired(USERS.ROLE),
        )
}
