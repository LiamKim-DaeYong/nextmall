package com.nextmall.checkout.application

import com.nextmall.checkout.fixture.createCheckoutCommand
import com.nextmall.checkout.fixture.lineItemCommand
import com.nextmall.checkout.fixture.updateCheckoutCommand
import com.nextmall.checkout.infrastructure.persistence.jooq.CheckoutJooqRepository
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutEntity
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutJpaRepository
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutLineItemEntity
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutPaymentHandlerEntity
import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.common.identifier.IdGenerator
import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.Optional

class CheckoutServiceTest :
    FunSpec({
        val idGenerator = mockk<IdGenerator>()
        val checkoutJpaRepository = mockk<CheckoutJpaRepository>()
        val checkoutJooqRepository = mockk<CheckoutJooqRepository>()
        val service = CheckoutService(idGenerator, checkoutJpaRepository, checkoutJooqRepository)

        test("체크아웃 생성 시 라인 아이템 합계가 totals에 반영된다") {
            val command =
                createCheckoutCommand(
                    lineItems =
                        listOf(
                            lineItemCommand(id = "line-1", quantity = 2, amount = 1000),
                            lineItemCommand(id = "line-2", quantity = 1, amount = 1500),
                        ),
                )
            val entitySlot = slot<CheckoutEntity>()

            every { idGenerator.generate() } returnsMany listOf(1L, 2L, 3L, 4L)
            every { checkoutJpaRepository.save(capture(entitySlot)) } answers { entitySlot.captured }

            val checkout = service.createCheckout(command)

            entitySlot.captured.subtotalAmount shouldBe 3500
            entitySlot.captured.taxAmount shouldBe 0
            entitySlot.captured.shippingAmount shouldBe 0
            entitySlot.captured.discountAmount shouldBe 0
            entitySlot.captured.totalAmount shouldBe 3500

            checkout.totals.subtotal.amount shouldBe 3500
            checkout.totals.total.amount shouldBe 3500
        }

        test("체크아웃 생성 시 라인 아이템이 비어 있으면 예외가 발생한다") {
            val command = createCheckoutCommand(lineItems = emptyList())

            shouldThrow<IllegalArgumentException> {
                service.createCheckout(command)
            }
        }

        test("체크아웃 생성 시 통화가 비어 있으면 예외가 발생한다") {
            val command = createCheckoutCommand(currency = "")

            shouldThrow<IllegalArgumentException> {
                service.createCheckout(command)
            }
        }

        test("체크아웃 생성 시 라인 아이템 통화가 다르면 예외가 발생한다") {
            val command =
                createCheckoutCommand(
                    lineItems =
                        listOf(
                            lineItemCommand(id = "line-1", quantity = 1, amount = 1000, currency = "KRW"),
                            lineItemCommand(id = "line-2", quantity = 1, amount = 1000, currency = "USD"),
                        ),
                    currency = "KRW",
                )

            shouldThrow<IllegalArgumentException> {
                service.createCheckout(command)
            }
        }

        test("체크아웃 수정 시 라인 아이템 기준으로 합계를 다시 계산한다") {
            val existingEntity =
                checkoutEntityFixture(
                    lineItems =
                        mutableListOf(
                            checkoutLineItemEntity(lineItemId = "old-1", quantity = 1, amount = 1000),
                        ),
                    subtotalAmount = 1000,
                    totalAmount = 1000,
                )
            val command =
                updateCheckoutCommand(
                    lineItems =
                        listOf(
                            lineItemCommand(id = "line-1", quantity = 3, amount = 1000),
                            lineItemCommand(id = "line-2", quantity = 2, amount = 500),
                        ),
                )
            val entitySlot = slot<CheckoutEntity>()

            every { checkoutJpaRepository.findById(existingEntity.id) } returns Optional.of(existingEntity)
            every { idGenerator.generate() } returnsMany listOf(10L, 11L, 12L)
            every { checkoutJpaRepository.save(capture(entitySlot)) } answers { entitySlot.captured }

            val updated = service.updateCheckout(existingEntity.id, command)

            entitySlot.captured.subtotalAmount shouldBe 4000
            entitySlot.captured.totalAmount shouldBe 4000
            updated.totals.subtotal.amount shouldBe 4000
            updated.totals.total.amount shouldBe 4000
        }

        test("체크아웃 수정 시 통화가 다르면 예외가 발생한다") {
            val existingEntity = checkoutEntityFixture()
            val command =
                updateCheckoutCommand(
                    lineItems =
                        listOf(
                            lineItemCommand(id = "line-1", quantity = 1, amount = 1000, currency = "USD"),
                        ),
                )

            every { checkoutJpaRepository.findById(existingEntity.id) } returns Optional.of(existingEntity)

            shouldThrow<IllegalArgumentException> {
                service.updateCheckout(existingEntity.id, command)
            }
        }

        test("완료된 체크아웃은 취소할 수 없다") {
            val existingEntity = checkoutEntityFixture(status = CheckoutStatus.COMPLETED)

            every { checkoutJpaRepository.findById(existingEntity.id) } returns Optional.of(existingEntity)

            shouldThrow<IllegalArgumentException> {
                service.cancelCheckout(existingEntity.id)
            }
        }

        test("취소된 체크아웃은 완료할 수 없다") {
            val existingEntity = checkoutEntityFixture(status = CheckoutStatus.CANCELED)
            val command =
                com.nextmall.checkout.application.command.CompleteCheckoutCommand(
                    payment =
                        com.nextmall.checkout.application.command.PaymentCommand(
                            handlers =
                                listOf(
                                    com.nextmall.checkout.application.command.PaymentHandlerCommand(
                                        type = "card",
                                        provider = "internal",
                                    ),
                                ),
                        ),
                    confirm = true,
                )

            every { checkoutJpaRepository.findById(existingEntity.id) } returns Optional.of(existingEntity)

            shouldThrow<IllegalArgumentException> {
                service.completeCheckout(existingEntity.id, command)
            }
        }
    })

private fun checkoutEntityFixture(
    id: String = "chk_1",
    status: CheckoutStatus = CheckoutStatus.INCOMPLETE,
    currency: String = "KRW",
    lineItems: MutableList<CheckoutLineItemEntity> = mutableListOf(),
    subtotalAmount: Long = 0,
    totalAmount: Long = 0,
) = CheckoutEntity(
    id = id,
    status = status,
    currency = currency,
    buyerId = null,
    buyerEmail = null,
    buyerName = null,
    shippingLine1 = null,
    shippingLine2 = null,
    shippingCity = null,
    shippingRegion = null,
    shippingPostalCode = null,
    shippingCountry = null,
    billingLine1 = null,
    billingLine2 = null,
    billingCity = null,
    billingRegion = null,
    billingPostalCode = null,
    billingCountry = null,
    subtotalAmount = subtotalAmount,
    taxAmount = 0,
    shippingAmount = 0,
    discountAmount = 0,
    totalAmount = totalAmount,
    termsUrl = null,
    privacyUrl = null,
    refundUrl = null,
    expiresAt = Instant.now(),
    paymentType = "card",
    paymentProvider = "internal",
    paymentHandlers =
        mutableListOf(
            CheckoutPaymentHandlerEntity(
                id = 1L,
                type = "card",
                provider = "internal",
                sortOrder = 0,
            ),
        ),
    lineItems = lineItems,
)

private fun checkoutLineItemEntity(
    id: Long = 1L,
    lineItemId: String,
    title: String = "테스트 상품",
    quantity: Int,
    amount: Long,
    currency: String = "KRW",
) = CheckoutLineItemEntity(
    id = id,
    lineItemId = lineItemId,
    title = title,
    quantity = quantity,
    priceAmount = amount,
    priceCurrency = currency,
    imageUrl = null,
)
