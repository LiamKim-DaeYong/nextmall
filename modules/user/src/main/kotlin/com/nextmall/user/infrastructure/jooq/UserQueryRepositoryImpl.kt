package com.nextmall.user.infrastructure.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.common.data.jooq.util.getUtcFromOffset
import com.nextmall.jooq.tables.references.USERS
import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.application.port.output.UserQueryRepository
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(
    dsl: DSLContext
) : JooqRepository(dsl), UserQueryRepository {

    private val fields = arrayOf(
        USERS.ID,
        USERS.EMAIL,
        USERS.NICKNAME,
        USERS.ROLE,
        USERS.PROVIDER,
        USERS.CREATED_AT,
    )

    override fun findByEmail(email: String): UserView? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .limit(1)
            .fetchOne { it.toUserView() }

    override fun findById(id: Long): UserView? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.ID.eq(id))
            .limit(1)
            .fetchOne { it.toUserView() }

    private fun Record.toUserView(): UserView =
        UserView(
            id = getRequired(USERS.ID),
            email = getRequired(USERS.EMAIL),
            nickname = getRequired(USERS.NICKNAME),
            role = getRequired(USERS.ROLE),
            provider = getRequired(USERS.PROVIDER),
            createdAt = getUtcFromOffset(USERS.CREATED_AT)
        )
}
