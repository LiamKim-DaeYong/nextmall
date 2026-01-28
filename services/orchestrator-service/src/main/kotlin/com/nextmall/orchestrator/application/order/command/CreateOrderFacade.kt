package com.nextmall.orchestrator.application.order.command

import com.nextmall.common.redis.stock.StockCacheRepository
import com.nextmall.common.redis.stock.StockDecreaseResult
import com.nextmall.orchestrator.application.order.exception.InsufficientStockException
import com.nextmall.orchestrator.client.order.OrderServiceClient
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.request.MoneyAmountClientRequest
import com.nextmall.orchestrator.client.order.request.OrderLineItemClientRequest
import com.nextmall.orchestrator.client.order.request.OrderTotalsClientRequest
import com.nextmall.orchestrator.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.math.BigDecimal
import java.util.UUID

@Component
class CreateOrderFacade(
    private val productServiceClient: ProductServiceClient,
    private val orderServiceClient: OrderServiceClient,
    private val stockCacheRepository: StockCacheRepository,
) {
    /**
     * 상품 조회와 주문 생성을 오케스트레이션한다.
     */
    fun createOrder(command: CreateOrderCommand): Mono<CreateOrderResult> {
        require(command.quantity > 0) { "Quantity must be positive" }

        return productServiceClient
            .getProduct(command.productId)
            .flatMap { product ->
                val totalPrice = product.price * command.quantity
                val checkoutId = UUID.randomUUID().toString()
                val currency = DEFAULT_CURRENCY
                val unitPrice = toMinorAmount(product.price.amount)
                val totalAmount = toMinorAmount(totalPrice.amount)
                val lineItem =
                    OrderLineItemClientRequest(
                        id = UUID.randomUUID().toString(),
                        productId = product.id.toString(),
                        title = product.name,
                        quantity = command.quantity,
                        price = MoneyAmountClientRequest(unitPrice, currency),
                        imageUrl = null,
                    )
                val zero = MoneyAmountClientRequest(0, currency)
                val totals =
                    OrderTotalsClientRequest(
                        subtotal = MoneyAmountClientRequest(totalAmount, currency),
                        tax = zero,
                        shipping = zero,
                        discount = zero,
                        total = MoneyAmountClientRequest(totalAmount, currency),
                    )
                val orderRequest =
                    CreateOrderSnapshotClientRequest(
                        checkoutId = checkoutId,
                        lineItems = listOf(lineItem),
                        totals = totals,
                        currency = currency,
                    )
                reserveStock(product.id, product.stock, command.quantity)
                    .then(
                        orderServiceClient
                            .createOrder(
                                request = orderRequest,
                            ).onErrorResume { ex ->
                                releaseStock(product.id, command.quantity)
                                    .then(Mono.error(ex))
                            },
                    )
            }.map { response -> CreateOrderResult(orderId = response.orderId) }
    }

    private fun reserveStock(
        productId: Long,
        currentStock: Int,
        quantity: Int,
    ): Mono<Unit> =
        Mono
            .fromCallable {
                stockCacheRepository.decreaseOrInit(productId, quantity, currentStock)
            }.subscribeOn(Schedulers.boundedElastic())
            .flatMap { result ->
                when (result) {
                    is StockDecreaseResult.Success -> Mono.empty()
                    StockDecreaseResult.InsufficientStock ->
                        Mono.error(InsufficientStockException(productId, quantity))
                    StockDecreaseResult.NotFound ->
                        Mono.error(IllegalStateException("Stock cache initialization failed for productId=$productId"))
                }
            }

    private fun releaseStock(
        productId: Long,
        quantity: Int,
    ): Mono<Int> =
        Mono
            .fromCallable { stockCacheRepository.increase(productId, quantity) }
            .subscribeOn(Schedulers.boundedElastic())

    private fun toMinorAmount(amount: BigDecimal): Long =
        amount
            .movePointRight(2)
            .longValueExact()

    companion object {
        private const val DEFAULT_CURRENCY = "USD"
    }
}
