package com.nextmall.user.domain.repository

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.testsupport.RepositoryTest
import com.nextmall.user.domain.model.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@RepositoryTest
class UserRepositoryIdTest(
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator,
) : FunSpec({

        test("Snowflake ID가 DB에 저장되고 조회된다") {
            // given
            val generatedId = idGenerator.generate()
            val user =
                User(
                    id = generatedId,
                    email = "snow@id.com",
                    password = "encoded",
                    nickname = "snowflake",
                )

            // when
            val saved = userRepository.save(user)
            val found = userRepository.findById(saved.id)

            // then
            found.shouldNotBeNull()
            found.get().id shouldBe generatedId
            found.get().email shouldBe "snow@id.com"
            found.get().nickname shouldBe "snowflake"
        }

        test("여러 ID가 고유하게 생성된다") {
            // given
            val id1 = idGenerator.generate()
            val id2 = idGenerator.generate()

            val user1 =
                User(
                    id = id1,
                    email = "test1@test.com",
                    password = "encoded",
                    nickname = "user1",
                )

            val user2 =
                User(
                    id = id2,
                    email = "test2@test.com",
                    password = "encoded",
                    nickname = "user2",
                )

            // when
            val saved1 = userRepository.save(user1)
            val saved2 = userRepository.save(user2)

            // then
            saved1.id shouldNotBe saved2.id
            saved1.id.shouldNotBeNull()
            saved2.id.shouldNotBeNull()
        }

        test("존재하지 않는 ID로 조회하면 빈 Optional 반환") {
            // given
            val nonExistentId = idGenerator.generate()

            // when
            val result = userRepository.findById(nonExistentId)

            // then
            result.isEmpty shouldBe true
        }
    })
