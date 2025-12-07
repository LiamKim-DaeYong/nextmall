package com.nextmall.common.data.jooq

import org.jooq.DSLContext

abstract class JooqRepository(
    protected val dsl: DSLContext,
)
