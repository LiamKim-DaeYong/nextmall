package com.nextmall.product.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.util.Money
import com.nextmall.common.web.mvc.authorization.exception.AccessDeniedException
import com.nextmall.product.domain.SaleStatus
import com.nextmall.product.domain.exception.ProductNotFoundException
import com.nextmall.product.fixture.productFixture
import com.nextmall.product.infrastructure.persistence.jooq.ProductJooqRepository
import com.nextmall.product.infrastructure.persistence.jpa.ProductJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class ProductServiceTest :
    FunSpec({
        val idGenerator = mockk<IdGenerator>()
        val productJpaRepository = mockk<ProductJpaRepository>()
        val productJooqRepository = mockk<ProductJooqRepository>()
        val service = ProductService(idGenerator, productJpaRepository, productJooqRepository)

        test("재고 차감 시 재고가 0이면 SOLD_OUT으로 전환된다") {
            val product = productFixture(stock = 3)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.decreaseStock(product.id, 3)

            product.stock shouldBe 0
            product.saleStatus shouldBe SaleStatus.SOLD_OUT
        }

        test("재고 차감 수량이 0 이하이면 예외가 발생한다") {
            shouldThrow<IllegalArgumentException> {
                service.decreaseStock(1L, 0)
            }
        }

        test("재고 차감 대상이 없으면 예외가 발생한다") {
            every { productJpaRepository.findById(999L) } returns Optional.empty()

            shouldThrow<ProductNotFoundException> {
                service.decreaseStock(999L, 1)
            }
        }

        test("상품 수정 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.updateProduct(
                    productId = product.id,
                    name = "updated",
                    description = null,
                    price = Money.of("10.00"),
                    stock = 5,
                    category = null,
                    sellerId = product.sellerId + 1,
                )
            }
        }

        test("상품 삭제 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.deleteProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 삭제 대상이 없으면 예외가 발생한다") {
            every { productJpaRepository.findById(999L) } returns Optional.empty()

            shouldThrow<ProductNotFoundException> {
                service.deleteProduct(999L, 1L)
            }
        }

        test("상품 삭제 시 삭제 상태로 전환된다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.SUSPENDED }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.deleteProduct(product.id, product.sellerId)

            product.isDeleted shouldBe true
            product.displayStatus shouldBe com.nextmall.product.domain.DisplayStatus.HIDDEN
        }

        test("상품 복구 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5).apply { isDeleted = true }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.restoreProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 복구 시 삭제 상태가 해제된다") {
            val product = productFixture(stock = 5).apply { isDeleted = true }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.restoreProduct(product.id, product.sellerId)

            product.isDeleted shouldBe false
        }

        test("상품 중지 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.suspendProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 중지 시 판매 상태가 중지로 전환된다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.suspendProduct(product.id, product.sellerId)

            product.saleStatus shouldBe SaleStatus.SUSPENDED
        }

        test("상품 재개 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.SUSPENDED }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.resumeProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 재개 시 판매 상태가 ON_SALE로 전환된다") {
            val product = productFixture(stock = 5).apply { saleStatus = SaleStatus.SUSPENDED }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.resumeProduct(product.id, product.sellerId)

            product.saleStatus shouldBe SaleStatus.ON_SALE
        }

        test("상품 노출 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.showProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 노출 시 표시 상태가 VISIBLE로 전환된다") {
            val product =
                productFixture(stock = 5).apply {
                    displayStatus = com.nextmall.product.domain.DisplayStatus.HIDDEN
                }
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.showProduct(product.id, product.sellerId)

            product.displayStatus shouldBe com.nextmall.product.domain.DisplayStatus.VISIBLE
        }

        test("상품 숨김 시 판매자가 다르면 인가 예외가 발생한다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            shouldThrow<AccessDeniedException> {
                service.hideProduct(product.id, product.sellerId + 1)
            }
        }

        test("상품 숨김 시 표시 상태가 HIDDEN으로 전환된다") {
            val product = productFixture(stock = 5)
            every { productJpaRepository.findById(product.id) } returns Optional.of(product)

            service.hideProduct(product.id, product.sellerId)

            product.displayStatus shouldBe com.nextmall.product.domain.DisplayStatus.HIDDEN
        }
    })
