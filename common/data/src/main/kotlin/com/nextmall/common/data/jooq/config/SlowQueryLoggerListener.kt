package com.nextmall.common.data.jooq.config

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.slf4j.LoggerFactory

class SlowQueryLoggerListener(
    private val thresholdMs: Long = 2000,
) : ExecuteListener {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun executeStart(ctx: ExecuteContext) {
        ctx.data("startTime", System.currentTimeMillis())
    }

    override fun executeEnd(ctx: ExecuteContext) {
        val start = ctx.data("startTime") as? Long ?: return
        val duration = System.currentTimeMillis() - start

        if (duration <= thresholdMs) return

        val query = ctx.query()
        val bindings = query?.bindValues
            ?.joinToString(prefix = "[", postfix = "]") { it?.toString() ?: "null" }
            ?: ""

        if (log.isWarnEnabled) {
            log.warn(
                """
                [SLOW QUERY] --------------------------------------------
                Time: ${duration}ms
                SQL: ${ctx.sql()}
                Bindings: $bindings
                ---------------------------------------------------------
                """.trimIndent()
            )
        }
    }
}
