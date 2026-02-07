package com.nextmall.product.domain

import com.nextmall.product.fixture.productFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProductTest :
    FunSpec({
        test("재고 증가 시 SOLD_OUT이면 ON_SALE로 전환된다") {
            val product = productFixture(stock = 0).apply { saleStatus = SaleStatus.SOLD_OUT }

            product.increaseStock(3)

            product.stock shouldBe 3
            product.saleStatus shouldBe SaleStatus.ON_SALE
        }

        test("재고 증가 수량이 0 이하이면 예외가 발생한다") {
            val product = productFixture(stock = 1)

            shouldThrow<IllegalArgumentException> {
                product.increaseStock(0)
            }
        }

        test("재고 차감 시 재고가 정상적으로 감소한다") {
            val product = productFixture(stock = 5)

            product.decreaseStock(2)

            product.stock shouldBe 3
            product.saleStatus shouldBe SaleStatus.ON_SALE
        }

        test("재고 차감 후 재고가 0이면 SOLD_OUT으로 전환된다") {
            val product = productFixture(stock = 3)

            product.decreaseStock(3)

            product.stock shouldBe 0
            product.saleStatus shouldBe SaleStatus.SOLD_OUT
        }

        test("재고 차감 수량이 0 이하이면 예외가 발생한다") {
            val product = productFixture(stock = 5)

            shouldThrow<IllegalArgumentException> {
                product.decreaseStock(0)
            }
        }

        test("재고 차감 시 재고가 부족하면 예외가 발생한다") {
            val product = productFixture(stock = 2)

            shouldThrow<IllegalArgumentException> {
                product.decreaseStock(3)
            }
        }

        test("상품 수정 시 재고와 상태가 함께 갱신된다") {
            val product = productFixture(stock = 1).apply { saleStatus = SaleStatus.ON_SALE }

            product.update(
                name = "updated",
                description = "updated-desc",
                price =
                    com.nextmall.common.util.Money
                        .of("9.99"),
                stock = 0,
                category = "updated-cat",
            )

            product.stock shouldBe 0
            product.saleStatus shouldBe SaleStatus.SOLD_OUT
        }

        test("상품 수정 시 재고가 음수면 예외가 발생한다") {
            val product = productFixture(stock = 1)

            shouldThrow<IllegalArgumentException> {
                product.update(
                    name = "updated",
                    description = null,
                    price =
                        com.nextmall.common.util.Money
                            .of("9.99"),
                    stock = -1,
                    category = null,
                )
            }
        }

        test("판매 중지는 판매 상태를 SUSPENDED로 전환한다") {
            val product = productFixture(stock = 5)

            product.suspend()

            product.saleStatus shouldBe SaleStatus.SUSPENDED
        }

        test("판매 중지는 삭제된 상품이면 예외가 발생한다") {
            val product = productFixture(stock = 5).apply { isDeleted = true }

            shouldThrow<IllegalArgumentException> {
                product.suspend()
            }
        }

        test("판매 재개는 SUSPENDED 상태에서만 ON_SALE로 복구된다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.SUSPENDED }

            product.resume()

            product.saleStatus shouldBe SaleStatus.ON_SALE
        }

        test("판매 재개는 재고가 0이면 예외가 발생한다") {
            val product = productFixture(stock = 0).apply { saleStatus = SaleStatus.SUSPENDED }

            shouldThrow<IllegalArgumentException> {
                product.resume()
            }
        }

        test("상품 숨김은 표시 상태를 HIDDEN으로 전환한다") {
            val product = productFixture(stock = 5)

            product.hide()

            product.displayStatus shouldBe DisplayStatus.HIDDEN
        }

        test("상품 노출은 표시 상태를 VISIBLE로 전환한다") {
            val product = productFixture(stock = 5).apply { displayStatus = DisplayStatus.HIDDEN }

            product.show()

            product.displayStatus shouldBe DisplayStatus.VISIBLE
        }

        test("상품 삭제는 SUSPENDED 상태에서만 가능하다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.SUSPENDED }

            product.delete()

            product.isDeleted shouldBe true
            product.displayStatus shouldBe DisplayStatus.HIDDEN
        }

        test("상품 삭제는 ON_SALE 상태면 예외가 발생한다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.ON_SALE }

            shouldThrow<IllegalArgumentException> {
                product.delete()
            }
        }

        test("상품 복구는 삭제된 상품을 복구한다") {
            val product = productFixture(stock = 5).apply { isDeleted = true }

            product.restore()

            product.isDeleted shouldBe false
        }

        test("상품 복구는 삭제되지 않은 상품이면 예외가 발생한다") {
            val product = productFixture(stock = 5)

            shouldThrow<IllegalArgumentException> {
                product.restore()
            }
        }
    })
