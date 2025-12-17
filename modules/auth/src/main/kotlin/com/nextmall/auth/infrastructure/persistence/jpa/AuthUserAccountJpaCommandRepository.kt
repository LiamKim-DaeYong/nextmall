package com.nextmall.auth.infrastructure.persistence.jpa

import com.nextmall.auth.domain.entity.AuthUserAccount
import com.nextmall.auth.port.output.account.AuthUserAccountCommandPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataAuthUserAccountJpaRepository : JpaRepository<AuthUserAccount, Long>

@Repository
class AuthUserAccountJpaCommandRepository(
    private val jpa: SpringDataAuthUserAccountJpaRepository,
) : AuthUserAccountCommandPort {
    override fun saveAndFlush(authUserAccount: AuthUserAccount): AuthUserAccount =
        jpa.saveAndFlush(authUserAccount)
}
