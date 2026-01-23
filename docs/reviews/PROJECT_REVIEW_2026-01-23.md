# NextMall Project Review (HEAD baseline) - 2026-01-23

Scope
- Reviewed committed code at git HEAD only (working tree changes excluded).
- Focused on security, consistency, and quality risks visible in current sources.

Summary
- High: 3
- Medium: 4
- Low: 2

High
1) Checkout service is not wired to Passport token security
   - Evidence: Other services explicitly import PassportTokenSecurityConfig but checkout-service has no equivalent SecurityConfig.
   - Risk: Requests may be handled by Spring Security defaults (basic login) or be effectively misconfigured; internal Passport auth is not enforced consistently.
   - References:
     - services/user-service/src/main/kotlin/com/nextmall/user/config/SecurityConfig.kt:1-11
     - services/order-service/src/main/kotlin/com/nextmall/order/config/SecurityConfig.kt:1-11
     - services/product-service/src/main/kotlin/com/nextmall/product/config/SecurityConfig.kt:1-11

2) Order ownership checks are missing on read/cancel
   - Evidence: TODO comments note missing ownership validation in getOrder and cancelOrder.
   - Risk: Any authenticated user can access or cancel another user’s order.
   - Reference:
     - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:25-61

3) Product update/delete lacks authorization context
   - Evidence: update/delete endpoints do not take CurrentUser or check ownership/roles, while create does.
   - Risk: Any authenticated user can modify or delete any product.
   - Reference:
     - services/product-service/src/main/kotlin/com/nextmall/product/presentation/controller/ProductController.kt:43-83

Medium
4) Checkout request validation does not cascade into nested objects
   - Evidence: Missing @Valid on lists and nested DTOs; quantity/amount constraints are minimal or absent.
   - Risk: Invalid or inconsistent payloads can slip into commands (nulls, zero/negative amounts, empty handlers).
   - References:
     - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/CheckoutRequests.kt:10-31
     - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/LineItemRequest.kt:7-16
     - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/MoneyRequest.kt:7-11
     - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/PaymentRequest.kt:8-17

5) Money model diverges across modules
   - Evidence: common Money uses BigDecimal with scale=2; checkout Money uses Long + currency.
   - Risk: Serialization and business rules differ by service; cross-service contracts can drift.
   - References:
     - common/util/src/main/kotlin/com/nextmall/common/util/Money.kt:6-33
     - services/checkout-service/src/main/kotlin/com/nextmall/checkout/domain/model/Money.kt:3-5

6) API payload casing is inconsistent
   - Evidence: checkout-service forces SNAKE_CASE, others use defaults.
   - Risk: Clients and BFF need per-service JSON conventions; increases integration friction.
   - Reference:
     - services/checkout-service/src/main/resources/application.yml:7-8

7) Response ID types are inconsistent
   - Evidence: user response uses String id, while order/product use Long.
   - Risk: Frontend/BFF DTO mapping churn; potential ID format ambiguity.
   - References:
     - services/user-service/src/main/kotlin/com/nextmall/user/presentation/response/UserViewResponse.kt:5-14
     - services/product-service/src/main/kotlin/com/nextmall/product/presentation/response/ProductViewResponse.kt:6-13
     - services/order-service/src/main/kotlin/com/nextmall/order/presentation/response/OrderViewResponse.kt:7-14

Low
8) Gateway error responses do not follow common error contract
   - Evidence: Gateway returns BAD_GATEWAY with CommonErrorCode.INTERNAL_ERROR and a fixed message, unlike other services that use the error code message and status mapping.
   - Risk: Client error handling becomes inconsistent; observability is reduced.
   - References:
     - services/api-gateway/src/main/kotlin/com/nextmall/apigateway/exception/GatewayGlobalExceptionHandler.kt:15-27
     - services/user-service/src/main/kotlin/com/nextmall/user/exception/UserGlobalExceptionHandler.kt:18-45

9) Documentation mismatch: BFF labeled as MVC but implemented as WebFlux
   - Evidence: README/CLAUDE say MVC; build.gradle uses spring-boot-starter-webflux.
   - Risk: Onboarding confusion and wrong assumptions during feature work.
   - References:
     - README.md:141
     - CLAUDE.md:25
     - services/bff-service/build.gradle.kts:21

Notes
- Checkout controller returns domain models directly (Checkout/Order) rather than response DTOs. This is a consistency/contract choice to revisit if you want tighter API stability.
  - Reference: services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/controller/CheckoutController.kt:26-72

Suggested next steps
1) Add PassportTokenSecurityConfig import for checkout-service (align with user/order/product).
2) Implement ownership/role checks for order and product modifications.
3) Add @Valid and numeric constraints in checkout requests; define a shared validation policy.
4) Decide on a unified Money and ID representation for external APIs.
5) Align JSON naming strategy and update docs to match actual runtime stack.

Appendix: Scope reminder
- Working tree changes were not reviewed; re-run after stabilizing those changes if needed.
