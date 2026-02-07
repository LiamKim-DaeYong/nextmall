package com.nextmall.orchestrator.application.order.command

import com.nextmall.common.identifier.IdGenerator
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
import java.math.BigDecimal
import java.util.Currency
import java.util.UUID

@Component
class CreateOrderFacade(
    private val idGenerator: IdGenerator,
    private val productServiceClient: ProductServiceClient,
    private val orderServiceClient: OrderServiceClient,
    private val stockCacheRepository: StockCacheRepository,
) {
    /**
     * 상품 조회와 주문 생성을 오케스트레이션한다.
     */
    fun createOrder(command: CreateOrderCommand): CreateOrderResult {
        require(command.quantity > 0) { "Quantity must be positive" }

        val product = productServiceClient.getProduct(command.productId)
        val totalPrice = product.price * command.quantity
        val checkoutId = UUID.randomUUID().toString()
        val currency = product.currency ?: DEFAULT_CURRENCY
        val unitPrice = toMinorAmount(product.price.amount, currency)
        val totalAmount = toMinorAmount(totalPrice.amount, currency)

        val lineItem =
            OrderLineItemClientRequest(
                lineItemId = idGenerator.generate().toString(),
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

        return try {
            val response =
                orderServiceClient.createOrder(
                    request = orderRequest,
                )
            CreateOrderResult(orderId = response.orderId)
        } catch (ex: Exception) {
            try {
                releaseStock(product.id, command.quantity)
            } catch (releaseEx: Exception) {
                ex.addSuppressed(releaseEx)
            }
            throw ex
        }
    }

    private fun reserveStock(
        productId: Long,
        currentStock: Int,
        quantity: Int,
    ) {
        val result = stockCacheRepository.decreaseOrInit(productId, quantity, currentStock)
        when (result) {
            is StockDecreaseResult.Success -> Unit
            StockDecreaseResult.InsufficientStock ->
                throw InsufficientStockException(productId, quantity)
            StockDecreaseResult.NotFound ->
                throw IllegalStateException("Stock cache initialization failed for productId=$productId")
        }
    }

    private fun releaseStock(
        productId: Long,
        quantity: Int,
    ): Int =
        stockCacheRepository.increase(productId, quantity)

    /**
     * 금액을 해당 통화의 최소 단위(minor unit)로 변환한다.
     * - KRW, JPY 등 소수점 없는 통화: 그대로 반환
     * - USD, EUR 등 2자리 소수점 통화: 100을 곱함
     */
    private fun toMinorAmount(amount: BigDecimal, currencyCode: String): Long {
        val fractionDigits = Currency.getInstance(currencyCode).defaultFractionDigits
        return amount
            .movePointRight(fractionDigits)
            .longValueExact()
    }

    companion object {
        private const val DEFAULT_CURRENCY = "KRW"
    }
}
