package com.nextmall.order.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.kafka.event.order.OrderCreatedEvent
import com.nextmall.common.kafka.producer.EventPublisher
import com.nextmall.order.domain.OrderEntity
import com.nextmall.order.domain.OrderStatus
import com.nextmall.order.fixture.createOrderSnapshotRequest
import com.nextmall.order.fixture.orderLineItemRequest
import com.nextmall.order.presentation.dto.MoneyAmount
import com.nextmall.order.presentation.dto.OrderFulfillment
import com.nextmall.order.presentation.dto.OrderLineItem
import com.nextmall.order.presentation.dto.OrderTotals
import com.nextmall.order.infrastructure.persistence.jpa.OrderJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import java.util.Optional

class OrderServiceTest :
    FunSpec({
        val idGenerator = mockk<IdGenerator>()
        val orderJpaRepository = mockk<OrderJpaRepository>()
        val objectMapper = mockk<ObjectMapper>()
        val eventPublisher = mockk<EventPublisher>(relaxed = true)
        val service = OrderService(idGenerator, orderJpaRepository, objectMapper, eventPublisher)

        beforeTest {
            clearMocks(eventPublisher)
        }

        test("주문 생성 시 저장 후 라인 아이템 수만큼 이벤트를 발행한다") {
            val request =
                createOrderSnapshotRequest(
                    lineItems =
                        listOf(
                            orderLineItemRequest(productId = "1", quantity = 2),
                            orderLineItemRequest(lineItemId = "line-2", productId = "2", quantity = 1),
                        ),
                )
            val lineItems =
                listOf(
                    OrderLineItem(
                        lineItemId = "line-1",
                        productId = "1",
                        title = "테스트 상품",
                        quantity = 2,
                        price = MoneyAmount(amount = 5000, currency = "KRW"),
                        imageUrl = "https://example.com/images/1",
                    ),
                    OrderLineItem(
                        lineItemId = "line-2",
                        productId = "2",
                        title = "테스트 상품",
                        quantity = 1,
                        price = MoneyAmount(amount = 5000, currency = "KRW"),
                        imageUrl = "https://example.com/images/1",
                    ),
                )
            val totals =
                OrderTotals(
                    subtotal = MoneyAmount(amount = 10000, currency = "KRW"),
                    tax = MoneyAmount(amount = 1000, currency = "KRW"),
                    shipping = MoneyAmount(amount = 2500, currency = "KRW"),
                    discount = MoneyAmount(amount = 0, currency = "KRW"),
                    total = MoneyAmount(amount = 13500, currency = "KRW"),
                )
            val fulfillment = OrderFulfillment()
            val adjustments = emptyList<Map<String, Any>>()
            val entitySlot = slot<OrderEntity>()

            every { idGenerator.generate() } returns 100L
            every { orderJpaRepository.save(capture(entitySlot)) } answers { entitySlot.captured }
            every { objectMapper.writeValueAsString(any()) } answers {
                when (val value = firstArg<Any>()) {
                    is OrderTotals -> "totals-json"
                    is OrderFulfillment -> "fulfillment-json"
                    is List<*> -> if (value == lineItems) "line-items-json" else "adjustments-json"
                    else -> error("지원하지 않는 직렬화 타입입니다: ${value::class}")
                }
            }
            every { objectMapper.readValue("line-items-json", any<TypeReference<*>>()) } returns lineItems
            every { objectMapper.readValue("adjustments-json", any<TypeReference<*>>()) } returns adjustments
            every { objectMapper.readValue("totals-json", OrderTotals::class.java) } returns totals
            every { objectMapper.readValue("fulfillment-json", OrderFulfillment::class.java) } returns fulfillment

            val snapshot = service.createOrder(request)

            snapshot.orderId shouldBe 100L
            snapshot.checkoutId shouldBe "checkout-1"
            snapshot.lineItems shouldBe lineItems
            snapshot.totals shouldBe totals
            snapshot.fulfillment shouldBe fulfillment
            snapshot.adjustments shouldBe adjustments

            entitySlot.captured.status shouldBe OrderStatus.CONFIRMED

            verify(exactly = 1) {
                eventPublisher.publish(
                    "order.created",
                    match<OrderCreatedEvent> { it.orderId == 100L && it.productId == 1L && it.quantity == 2 },
                )
            }
            verify(exactly = 1) {
                eventPublisher.publish(
                    "order.created",
                    match<OrderCreatedEvent> { it.orderId == 100L && it.productId == 2L && it.quantity == 1 },
                )
            }
            verify(exactly = 1) { orderJpaRepository.save(any()) }
        }

        test("상품 ID가 숫자가 아니면 400 예외가 발생한다") {
            val request =
                createOrderSnapshotRequest(
                    lineItems =
                        listOf(
                            orderLineItemRequest(productId = "not-a-number", quantity = 1),
                        ),
                )
            val entitySlot = slot<OrderEntity>()

            every { idGenerator.generate() } returns 100L
            every { orderJpaRepository.save(capture(entitySlot)) } answers { entitySlot.captured }
            every { objectMapper.writeValueAsString(any()) } returns "json"

            val exception =
                shouldThrow<ResponseStatusException> {
                    service.createOrder(request)
                }

            exception.statusCode shouldBe HttpStatus.BAD_REQUEST
            verify(exactly = 0) { eventPublisher.publish(any(), any<OrderCreatedEvent>()) }
        }

        test("주문 조회 시 주문이 없으면 404 예외가 발생한다") {
            every { orderJpaRepository.findById(999L) } returns Optional.empty()

            val exception =
                shouldThrow<ResponseStatusException> {
                    service.getOrder(999L)
                }

            exception.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    })
