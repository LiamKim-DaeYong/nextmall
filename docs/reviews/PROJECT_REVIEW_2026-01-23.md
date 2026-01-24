# NextMall Project Review (HEAD baseline) - 2026-01-23

Scope
- Reviewed committed code at git HEAD only (working tree changes excluded).
- Focused on security, consistency, and quality risks visible in current sources.

Summary
- High: 3
- Medium: 15
- Low: 12

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

Quality / Convention (Additional)
10) Policy enforcement reads principal from authentication details
    - Evidence: PolicyEnforcementAspect casts authentication.details to AuthenticatedPrincipal.
    - Risk: If security filters set principal on authentication.principal (common pattern), policy evaluation will fail even for valid auth, causing false AccessDenied.
    - Reference:
      - common/authorization/src/main/kotlin/com/nextmall/common/authorization/aop/PolicyEnforcementAspect.kt:89-102

11) Order service DTO package and naming diverge from other services
    - Evidence: order-service uses presentation/dto and \*Snapshot naming, while other services use presentation/request|response with *Request/*Response.
    - Risk: Inconsistent API conventions increase onboarding cost and DTO mapping churn.
    - References:
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/CreateOrderSnapshotRequest.kt:1-22
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:3-34
      - services/product-service/src/main/kotlin/com/nextmall/product/presentation/request/CreateProductRequest.kt:1-22

12) Security configuration class naming is inconsistent
    - Evidence: auth-service uses WebSecurityConfig, while other services use SecurityConfig.
    - Risk: Reduced discoverability and inconsistent conventions for security-related files.
    - References:
      - services/auth-service/src/main/kotlin/com/nextmall/auth/config/WebSecurityConfig.kt:11-17
      - services/user-service/src/main/kotlin/com/nextmall/user/config/SecurityConfig.kt:8-11

13) Money DTO naming differs across services
    - Evidence: order-service uses MoneyAmount, checkout/common use Money.
    - Risk: Ambiguous naming makes cross-service payload mapping harder to reason about.
    - References:
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/MoneyAmount.kt:1-5
      - services/checkout-service/src/main/kotlin/com/nextmall/checkout/domain/model/Money.kt:1-5
      - common/util/src/main/kotlin/com/nextmall/common/util/Money.kt:6-33

14) Checkout response mapping logic is duplicated
    - Evidence: Checkout.toResponse() and CheckoutView.toResponse() duplicate the same field mapping.
    - Risk: Future changes can diverge or be missed in one path, increasing API inconsistency risk.
    - Reference:
      - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/response/CheckoutResponseMapper.kt:15-113

15) Order money amount request lacks numeric validation
    - Evidence: MoneyAmountRequest.amount has no constraints (e.g., @Min), while used in OrderTotalsRequest.
    - Risk: Negative or zero amounts can pass validation and create invalid order totals.
    - References:
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/MoneyAmountRequest.kt:1-9
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/OrderTotalsRequest.kt:1-19

16) Order snapshot uses untyped adjustments payload
    - Evidence: adjustments is declared as List<Map<String, Any>> in API DTO.
    - Risk: Weak typing makes client contracts unstable and complicates validation/serialization.
    - Reference:
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/OrderSnapshot.kt:1-11

17) Social sign-up request lacks validation
    - Evidence: SocialSignUpRequest has no validation annotations and controller does not use @Valid for that endpoint.
    - Risk: If the endpoint is implemented later, invalid payloads can slip through without guardrails.
    - References:
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/signup/SocialSignUpRequest.kt:1-7
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/SignUpController.kt:27-39

Architecture / Commonization (Additional)
18) Auth contract DTOs are duplicated between auth-service and BFF
    - Evidence: TokenResponse, LoginRequest, RefreshTokenRequest exist separately in both services with the same fields.
    - Risk: Contract drift between BFF and auth-service; changes require double updates and can break clients silently.
    - References:
      - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/response/token/TokenResponse.kt:1-6
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/response/login/TokenResponse.kt:1-14
      - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/token/LoginRequest.kt:1-13
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/login/LoginRequest.kt:1-21
      - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/token/RefreshTokenRequest.kt:1-8
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/token/RefreshTokenRequest.kt:1-8

19) AuthProvider enum is duplicated across services
    - Evidence: auth-service and BFF each define their own AuthProvider enum.
    - Risk: Adding or renaming providers requires synchronized changes; mismatches can cause runtime mapping failures.
    - References:
      - services/auth-service/src/main/kotlin/com/nextmall/auth/domain/account/AuthProvider.kt:1-11
      - services/bff-service/src/main/kotlin/com/nextmall/bff/client/auth/AuthProvider.kt:1-9

20) API route naming conventions are mixed within BFF
    - Evidence: BFF uses "/auth/tokens/refresh" and "/sign-up" (kebab case) in the same API surface.
    - Risk: Inconsistent routing style makes API harder to standardize and document.
    - References:
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/AuthController.kt:19-47
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/SignUpController.kt:18-43

21) Error-to-HTTP mapping differs by service
    - Evidence: user-service maps AuthorizationErrorCode to FORBIDDEN, while auth-service uses category mapping only.
    - Risk: Clients can receive different HTTP statuses for similar error categories across services.
    - References:
      - services/user-service/src/main/kotlin/com/nextmall/user/exception/HttpStatusMapper.kt:10-27
      - services/auth-service/src/main/kotlin/com/nextmall/auth/exception/HttpStatusMapper.kt:10-24

Documentation / Information Architecture (Additional)
22) Architecture evolution doc duplicates edge-auth decision details
    - Evidence: evolution.md restates Edge Authentication rationale and token flow, while ADR-007 is the authoritative decision record.
    - Risk: Two sources of truth can drift and confuse readers about the current policy.
    - References:
      - docs/architecture/evolution.md:53-130
      - docs/decisions/ADR-007-Edge-Authentication.md:9-171

23) UCP design overlaps with UCP Checkout/Order API docs
    - Evidence: ucp-design.md specifies checkout/order flows, fields, and rules that are also defined in dedicated API docs.
    - Risk: Contract details can diverge across documents during fast iteration.
    - References:
      - docs/architecture/ucp-design.md:26-79
      - docs/architecture/ucp-checkout-api.md:6-122
      - docs/architecture/ucp-order-api.md:9-76

24) Reviews are not linked from the docs index
    - Evidence: docs/README.md does not list docs/reviews while review documents exist in the tree.
    - Risk: Important quality baselines become hidden and are less likely to be maintained.
    - References:
      - docs/README.md:7-62
      - docs/reviews/PROJECT_REVIEW_2026-01-23.md:1-200

25) Error response contract is too minimal for operational use
    - Evidence: ErrorResponse contains only code/message, with no trace/correlation, path, or validation details.
    - Risk: Debugging and client-side error handling become harder; validation errors cannot be localized per field.
    - Reference:
      - common/exception/src/main/kotlin/com/nextmall/common/exception/ErrorResponse.kt:1-6

26) Global exception handling logic is duplicated per service
    - Evidence: auth-service and user-service each define similar GlobalExceptionHandler with near-identical mapping/response logic.
    - Risk: Error handling behavior can drift across services and adds maintenance overhead.
    - References:
      - services/auth-service/src/main/kotlin/com/nextmall/auth/exception/AuthGlobalExceptionHandler.kt:1-47
      - services/user-service/src/main/kotlin/com/nextmall/user/exception/UserGlobalExceptionHandler.kt:1-47

Quality / API Consistency (Additional)
27) BFF order endpoints accept userId from client input
    - Evidence: CreateOrderRequest includes userId and OrderController uses it directly to build the command; user-scoped query also uses path userId.
    - Risk: Without enforcing CurrentUser, clients can request/operate on other users’ orders; at minimum, this creates a contract that depends on client-supplied identity.
    - References:
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/order/CreateOrderRequest.kt:6-12
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:26-55

28) BFF create-order returns 200 OK instead of 201 Created
    - Evidence: createOrder maps result to ResponseEntity.ok, while the order-service uses CREATED.
    - Risk: Inconsistent REST semantics and client expectations across layers.
    - References:
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:26-38
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:25-34

29) User-scoped order endpoint path is non-standard
    - Evidence: BFF exposes GET /orders/users/{userId} instead of the more common /users/{userId}/orders style used in many REST conventions.
    - Risk: API surface becomes harder to standardize and document when additional user-scoped resources are added.
    - Reference:
      - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:49-55

30) Auth account creation request lacks provider validation
    - Evidence: CreateAuthAccountRequest.provider has no @NotNull validation.
    - Risk: Null provider can slip into the application layer and cause runtime errors or ambiguous account creation.
    - Reference:
      - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/account/CreateAuthAccountRequest.kt:7-16

Package Structure / Layering (Additional)
31) Service package structure is inconsistent across modules
    - Evidence: order-service uses presentation/dto with *Snapshot naming, while other services use presentation/request|response.
    - Risk: Inconsistent package conventions slow onboarding and increase DTO mapping mistakes during refactoring.
    - References:
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/CreateOrderSnapshotRequest.kt:1-22
      - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:3-34
      - services/product-service/src/main/kotlin/com/nextmall/product/presentation/request/CreateProductRequest.kt:1-22

32) Domain-to-API mapping is not consistently isolated
    - Evidence: checkout response mapping is centralized but duplicates domain/query mappings; other services rely on inline conversions.
    - Risk: API contract drift and missed fields during evolution.
    - Reference:
      - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/response/CheckoutResponseMapper.kt:15-113

33) Layer boundaries are not explicitly documented as rules
    - Evidence: No shared document exists that locks controller/application/domain/infrastructure boundaries as project-wide rules.
    - Risk: Future modules will diverge in structure and responsibility, increasing technical debt.
    - Reference:
      - docs/architecture/commonization-standards.md:1-68

Notes
- Checkout controller returns domain models directly (Checkout/Order) rather than response DTOs. This is a consistency/contract choice to revisit if you want tighter API stability.
    - Reference: services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/controller/CheckoutController.kt:26-72

Suggested next steps
1) Add PassportTokenSecurityConfig import for checkout-service (align with user/order/product).
2) Implement ownership/role checks for order and product modifications.
3) Add @Valid and numeric constraints in checkout requests; define a shared validation policy.
4) Decide on a unified Money and ID representation for external APIs.
5) Align JSON naming strategy and update docs to match actual runtime stack.
6) Align DTO/package naming conventions across services (request/response vs dto/snapshot).
7) Clarify principal extraction source (authentication.principal vs details) for policy enforcement.
8) Consolidate UCP documentation (single source of truth + API-only details split).
9) Add a docs index entry for reviews and define doc ownership/status rules.
10) Define MVP Kafka event contract and consumer role (ProductCreated → catalog-indexer cache update).

Appendix: Scope reminder
- Working tree changes were not reviewed; re-run after stabilizing those changes if needed.
