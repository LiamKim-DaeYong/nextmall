package com.nextmall.auth.application.command.account

import com.nextmall.auth.domain.entity.AuthUserAccount
import com.nextmall.auth.domain.model.AuthProvider
import com.nextmall.common.util.toUtc
import java.time.OffsetDateTime

data class CreateAuthUserAccountResult(
    val id: Long,
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun from(entity: AuthUserAccount) =
            CreateAuthUserAccountResult(
                id = entity.id,
                userId = entity.userId,
                provider = entity.provider,
                providerAccountId = entity.providerAccountId,
                createdAt = entity.createdAt.toUtc(),
            )
    }
}
