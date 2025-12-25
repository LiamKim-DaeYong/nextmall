package com.nextmall.user.application.command

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.application.command.create.CreateUserCommandParam
import com.nextmall.user.domain.User
import com.nextmall.user.port.output.UserCommandPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class CreateUserCommandHandlerTest :
    FunSpec({

        val userCommandPort = mockk<UserCommandPort>()
        val idGenerator = mockk<IdGenerator>()

        lateinit var handler: CreateUserCommandHandler

        beforeTest {
            clearMocks(userCommandPort, idGenerator)
            handler = CreateUserCommandHandler(userCommandPort, idGenerator)
        }

        test("사용자 생성 시 ID 생성 및 저장이 정상적으로 이루어진다") {
            // given
            every { idGenerator.generate() } returns 100L

            val savedUserSlot = slot<User>()
            every { userCommandPort.save(capture(savedUserSlot)) } answers {
                savedUserSlot.captured
            }

            // when
            val result =
                handler.create(
                    param = CreateUserCommandParam("nick", "a@b.com"),
                )

            // then
            savedUserSlot.captured.apply {
                nickname shouldBe "nick"
                email shouldBe "a@b.com"
                id shouldBe 100L
            }

            result.id shouldBe 100L
            result.email shouldBe "a@b.com"
        }
    })
