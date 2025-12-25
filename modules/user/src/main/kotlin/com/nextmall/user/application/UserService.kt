package com.nextmall.user.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.application.query.UserView
import com.nextmall.user.application.result.CreateUserResult
import com.nextmall.user.domain.User
import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.domain.model.UserStatus
import com.nextmall.user.infrastructure.persistence.jooq.UserJooqRepository
import com.nextmall.user.infrastructure.persistence.jpa.UserJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val idGenerator: IdGenerator,
    private val userJpaRepository: UserJpaRepository,
    private val userJooqRepository: UserJooqRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 조회
     */
    @Transactional(readOnly = true)
    fun getUser(userId: Long): UserView =
        userJooqRepository.findById(userId)
            ?: throw UserNotFoundException()

    /**
     * 사용자 생성 (회원가입 1단계)
     * - 초기 상태는 PENDING
     */
    @Transactional
    fun create(
        nickname: String,
        email: String?,
    ): CreateUserResult {
        val user =
            User(
                id = idGenerator.generate(),
                nickname = nickname,
                email = email,
                status = UserStatus.PENDING,
            )

        val saved = userJpaRepository.save(user)
        return CreateUserResult(saved)
    }

    /**
     * 사용자 활성화 (회원가입 완료)
     */
    @Transactional
    fun activate(userId: Long) {
        val user =
            userJpaRepository
                .findById(userId)
                .orElseThrow { UserNotFoundException() }

        user.activate()
    }

    /**
     * 회원가입 실패 처리
     */
    @Transactional
    fun markFailed(userId: Long) {
        val user =
            userJpaRepository
                .findById(userId)
                .orElseThrow { UserNotFoundException() }

        user.markSignupFailed()
    }
}
