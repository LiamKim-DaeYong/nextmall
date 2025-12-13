package com.nextmall.user.infrastructure.persistence.jpa

import com.nextmall.user.port.output.UserCommandPort
import com.nextmall.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
interface SpringDataUserJpaRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
}

@Repository
class UserJpaCommandRepository(
    private val jpa: SpringDataUserJpaRepository,
) : UserCommandPort {
    override fun save(user: User): User =
        jpa.save(user)

    override fun existsByEmail(email: String): Boolean =
        jpa.existsByEmail(email)

    override fun findById(userId: Long): User? =
        jpa.findById(userId).getOrNull()
}
