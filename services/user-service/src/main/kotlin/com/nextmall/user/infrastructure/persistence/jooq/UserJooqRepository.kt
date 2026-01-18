package com.nextmall.user.infrastructure.persistence.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.jooq.tables.references.USERS
import com.nextmall.user.application.query.UserView
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class UserJooqRepository(
    dsl: DSLContext
) : JooqRepository(dsl) {

    private val fields = arrayOf(
        USERS.ID,
        USERS.EMAIL,
        USERS.NICKNAME,
    )

    fun findById(id: Long): UserView? =
        dsl.select(*fields)
            .from(USERS)
            .where(USERS.ID.eq(id))
            .fetchOne { it.toUserView() }

    private fun Record.toUserView() =
        UserView(
            id = getRequired(USERS.ID),
            nickname = getRequired(USERS.NICKNAME),
            email = get(USERS.EMAIL),
        )
}
