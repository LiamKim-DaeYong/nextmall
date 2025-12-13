package com.nextmall.user.infrastructure.persistence.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.common.data.jooq.util.getUtcFromOffset
import com.nextmall.jooq.tables.references.USERS
import com.nextmall.user.port.output.UserQueryPort
import com.nextmall.user.application.query.UserContext
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class UserJooqQueryRepository(
    dsl: DSLContext
) : JooqRepository(dsl), UserQueryPort {

    private val fields = arrayOf(
        USERS.ID,
        USERS.EMAIL,
        USERS.NICKNAME,
        USERS.CREATED_AT,
    )

    override fun findByEmail(email: String): UserContext? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .limit(1)
            .fetchOne { it.toUserContext() }

    override fun findById(id: Long): UserContext? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.ID.eq(id))
            .limit(1)
            .fetchOne { it.toUserContext() }

    private fun Record.toUserContext() =
        UserContext(
            id = getRequired(USERS.ID),
            nickname = getRequired(USERS.NICKNAME),
            email = get(USERS.EMAIL),
            createdAt = getUtcFromOffset(USERS.CREATED_AT)
        )
}
