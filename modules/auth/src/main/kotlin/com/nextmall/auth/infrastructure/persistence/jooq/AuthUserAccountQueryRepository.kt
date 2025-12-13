package com.nextmall.auth.infrastructure.persistence.jooq

import com.nextmall.auth.application.query.account.AuthUserAccountContext
import com.nextmall.auth.domain.model.AuthProvider
import com.nextmall.auth.port.output.account.AuthUserAccountQueryPort
import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.jooq.tables.references.AUTH_USER_ACCOUNTS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class AuthUserAccountQueryRepository(
    dsl: DSLContext
) : JooqRepository(dsl), AuthUserAccountQueryPort {

    private val fields = arrayOf(
        AUTH_USER_ACCOUNTS.ID,
        AUTH_USER_ACCOUNTS.USER_ID,
        AUTH_USER_ACCOUNTS.PROVIDER,
        AUTH_USER_ACCOUNTS.PROVIDER_ACCOUNT_ID,
        AUTH_USER_ACCOUNTS.PASSWORD_HASH,
        AUTH_USER_ACCOUNTS.CREATED_AT,
    )

    override fun findByProviderAndAccountId(
        provider: AuthProvider,
        providerAccountId: String,
    ): AuthUserAccountContext? {
        return dsl.select(*fields)
            .from(AUTH_USER_ACCOUNTS)
            .where(
                AUTH_USER_ACCOUNTS.PROVIDER.eq(provider.name),
                AUTH_USER_ACCOUNTS.PROVIDER_ACCOUNT_ID.eq(providerAccountId),
            )
            .limit(1)
            .fetchOne { it.toAuthUserAccountView() }
    }

    private fun Record.toAuthUserAccountView() =
        AuthUserAccountContext(
            id = getRequired(AUTH_USER_ACCOUNTS.ID),
            userId = getRequired(AUTH_USER_ACCOUNTS.USER_ID),
            provider = AuthProvider.valueOf(
                getRequired(AUTH_USER_ACCOUNTS.PROVIDER)
            ),
            providerAccountId = getRequired(AUTH_USER_ACCOUNTS.PROVIDER_ACCOUNT_ID),
            passwordHash = get(AUTH_USER_ACCOUNTS.PASSWORD_HASH),
        )
}
