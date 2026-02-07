package com.nextmall.orchestrator.application.order.command

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.redis.stock.StockCacheRepository
import com.nextmall.common.redis.stock.StockDecreaseResult
import com.nextmall.orchestrator.application.order.exception.InsufficientStockException
import com.nextmall.orchestrator.client.order.OrderServiceClient
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.client.product.ProductServiceClient
import com.nextmall.orchestrator.fixture.createOrderCommand
import com.nextmall.orchestrator.fixture.productViewClientResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class CreateOrderFacadeTest :
    FunSpec({
        val idGenerator = mockk<IdGenerator>()
        val productServiceClient = mockk<ProductServiceClient>()
        val orderServiceClient = mockk<OrderServiceClient>()
        val stockCacheRepository = mockk<StockCacheRepository>()
        val facade = CreateOrderFacade(idGenerator, productServiceClient, orderServiceClient, stockCacheRepository)

        beforeTest {
            clearMocks(idGenerator, productServiceClient, orderServiceClient, stockCacheRepository)
        }

        test("재고 예약 후 주문이 생성된다") {
            val command = createOrderCommand(quantity = 2)
            val product =
                productViewClientResponse(
                    price =
                        com.nextmall.common.util.Money
                            .of("12.34"),
                )
            val requestSlot = slot<CreateOrderSnapshotClientRequest>()

            every { productServiceClient.getProduct(command.productId) } returns product
            every { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) } returns
                StockDecreaseResult.Success(remaining = 8)
            every { idGenerator.generate() } returns 1000L
            every { orderServiceClient.createOrder(capture(requestSlot)) } returns
                CreateOrderClientResponse(orderId = 55L)

            val result = facade.createOrder(command)

            result.orderId shouldBe 55L
            requestSlot.captured.currency shouldBe "USD"
            requestSlot.captured.lineItems
                .first()
                .price.amount shouldBe 1234
            requestSlot.captured.totals.total.amount shouldBe 2468

            verify(exactly = 1) { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) }
            verify(exactly = 0) { stockCacheRepository.increase(any(), any()) }
        }

        test("주문 생성이 실패하면 재고를 복구한다") {
            val command = createOrderCommand(quantity = 2)
            val product =
                productViewClientResponse(
                    price =
                        com.nextmall.common.util.Money
                            .of("12.34"),
                )

            every { productServiceClient.getProduct(command.productId) } returns product
            every { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) } returns
                StockDecreaseResult.Success(remaining = 8)
            every { idGenerator.generate() } returns 1000L
            every { orderServiceClient.createOrder(any()) } throws RuntimeException("order failed")
            every { stockCacheRepository.increase(product.id, command.quantity) } returns 10

            shouldThrow<RuntimeException> {
                facade.createOrder(command)
            }

            verify(exactly = 1) { stockCacheRepository.increase(product.id, command.quantity) }
        }

        test("재고가 부족하면 예외가 발생한다") {
            val command = createOrderCommand(quantity = 2)
            val product = productViewClientResponse()

            every { productServiceClient.getProduct(command.productId) } returns product
            every { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) } returns
                StockDecreaseResult.InsufficientStock
            every { idGenerator.generate() } returns 1000L

            shouldThrow<InsufficientStockException> {
                facade.createOrder(command)
            }

            verify(exactly = 0) { orderServiceClient.createOrder(any()) }
        }

        test("재고 캐시 초기화 실패 시 예외가 발생한다") {
            val command = createOrderCommand(quantity = 1)
            val product = productViewClientResponse()

            every { productServiceClient.getProduct(command.productId) } returns product
            every { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) } returns
                StockDecreaseResult.NotFound
            every { idGenerator.generate() } returns 1000L

            shouldThrow<IllegalStateException> {
                facade.createOrder(command)
            }

            verify(exactly = 0) { orderServiceClient.createOrder(any()) }
        }

        test("통화가 없으면 KRW로 처리한다") {
            val command = createOrderCommand(quantity = 2)
            val product =
                productViewClientResponse(
                    price =
                        com.nextmall.common.util.Money
                            .of("1000.00"),
                    currency = null,
                )
            val requestSlot = slot<CreateOrderSnapshotClientRequest>()

            every { productServiceClient.getProduct(command.productId) } returns product
            every { stockCacheRepository.decreaseOrInit(product.id, command.quantity, product.stock) } returns
                StockDecreaseResult.Success(remaining = 8)
            every { idGenerator.generate() } returns 1000L
            every { orderServiceClient.createOrder(capture(requestSlot)) } returns
                CreateOrderClientResponse(orderId = 77L)

            facade.createOrder(command)

            requestSlot.captured.currency shouldBe "KRW"
            requestSlot.captured.lineItems
                .first()
                .price.amount shouldBe 1000
            requestSlot.captured.totals.total.amount shouldBe 2000
        }
    })
