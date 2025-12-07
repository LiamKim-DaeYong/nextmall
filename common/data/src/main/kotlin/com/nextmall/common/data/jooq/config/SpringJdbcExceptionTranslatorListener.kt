package com.nextmall.common.data.jooq.config

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.springframework.jdbc.support.SQLExceptionTranslator

@Suppress("SqlSourceToSinkFlow")
class SpringJdbcExceptionTranslatorListener(
    private val translator: SQLExceptionTranslator
) : ExecuteListener {

    override fun exception(ctx: ExecuteContext) {
        val sqlEx = ctx.sqlException() ?: return
        val translated = translator.translate("jOOQ", ctx.sql(), sqlEx)

        if (translated != null) {
            ctx.exception(translated)
        }
    }
}
