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
            .fetchOne { it.toUserView() }

    override fun findById(id: Long): UserContext? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.ID.eq(id))
            .limit(1)
            .fetchOne { it.toUserView() }

    private fun Record.toUserView() =
        UserContext(
            id = getRequired(USERS.ID),
            email = getRequired(USERS.EMAIL),
            nickname = getRequired(USERS.NICKNAME),
            createdAt = getUtcFromOffset(USERS.CREATED_AT)
        )
}
