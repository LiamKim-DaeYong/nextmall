package com.nextmall.common.data.jooq.config

import org.jooq.ExecuteListenerProvider
import org.jooq.conf.RenderNameCase
import org.jooq.conf.RenderQuotedNames
import org.jooq.conf.Settings
import org.jooq.conf.StatementType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator

@Configuration
class JooqConfig {
    @Bean
    fun jooqSettings(): Settings =
        Settings().apply {
            renderQuotedNames = RenderQuotedNames.NEVER
            renderNameCase = RenderNameCase.AS_IS
            statementType = StatementType.PREPARED_STATEMENT
            fetchSize = 200
            queryTimeout = 3
        }

    @Bean
    fun jooqExecuteListeners(): Array<ExecuteListenerProvider> {
        val translator = SQLErrorCodeSQLExceptionTranslator()

        return arrayOf(
            ExecuteListenerProvider { SpringJdbcExceptionTranslatorListener(translator) },
            ExecuteListenerProvider { SlowQueryLoggerListener() }
        )
    }
}
