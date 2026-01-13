# E2E 테스트 시나리오

> **목적**: 완성된 시스템의 동작을 정의. 현재 구현 여부와 무관하게 "이렇게 되어야 한다"의 기준점.
>
> 테스트 실패 = 아직 구현 안 됨. 하나씩 통과시키면서 시스템 완성.

---

## 목차

1. [사용자 & 인증](#1-사용자--인증)
2. [상품 관리](#2-상품-관리)
3. [주문 플로우](#3-주문-플로우)
4. [재고 관리](#4-재고-관리)
5. [권한 & 보안](#5-권한--보안)
6. [에러 & 엣지케이스](#6-에러--엣지케이스)

---

## 1. 사용자 & 인증

### 1.1 회원가입

```gherkin
Feature: 회원가입

  Scenario: 구매자 회원가입
    Given path '/sign-up/local'
    And request
      """
      {
        "email": "buyer@example.com",
        "password": "Password1!",
        "name": "구매자",
        "role": "BUYER"
      }
      """
    When method POST
    Then status 201
    And match response.userId == '#number'

  Scenario: 판매자 회원가입
    Given path '/sign-up/local'
    And request
      """
      {
        "email": "seller@example.com",
        "password": "Password1!",
        "name": "판매자",
        "role": "SELLER",
        "businessNumber": "123-45-67890"
      }
      """
    When method POST
    Then status 201
    And match response.userId == '#number'

  Scenario: 이메일 중복 가입 실패
    Given 이미 'existing@example.com'으로 가입된 사용자가 있음
    When 같은 이메일로 가입 시도
    Then status 409
    And match response.code == 'DUPLICATE_EMAIL'

  Scenario: 비밀번호 정책 위반
    Given path '/sign-up/local'
    And request { email: 'test@example.com', password: '1234', name: '테스터' }
    When method POST
    Then status 400
    And match response.code == 'INVALID_PASSWORD'
```

### 1.2 로그인 & 토큰

```gherkin
Feature: 로그인

  Scenario: 로그인 성공
    Given path '/auth/login'
    And request { email: 'buyer@example.com', password: 'Password1!' }
    When method POST
    Then status 200
    And match response.accessToken == '#string'
    And match response.refreshToken == '#string'
    And match response.expiresIn == '#number'

  Scenario: 잘못된 비밀번호
    Given path '/auth/login'
    And request { email: 'buyer@example.com', password: 'wrongpassword' }
    When method POST
    Then status 401
    And match response.code == 'INVALID_CREDENTIALS'

  Scenario: 존재하지 않는 이메일
    Given path '/auth/login'
    And request { email: 'notexist@example.com', password: 'Password1!' }
    When method POST
    Then status 401
    And match response.code == 'INVALID_CREDENTIALS'

  Scenario: 토큰 갱신
    Given 유효한 refreshToken
    And path '/auth/tokens/refresh'
    And request { refreshToken: '#(refreshToken)' }
    When method POST
    Then status 200
    And match response.accessToken == '#string'

  Scenario: 만료된 리프레시 토큰으로 갱신 시도
    Given 만료된 refreshToken
    And path '/auth/tokens/refresh'
    And request { refreshToken: '#(expiredToken)' }
    When method POST
    Then status 401
    And match response.code == 'TOKEN_EXPIRED'

  Scenario: 로그아웃
    Given path '/auth/logout'
    And request { refreshToken: '#(refreshToken)' }
    When method POST
    Then status 204

    # 로그아웃된 토큰으로 갱신 시도
    Given path '/auth/tokens/refresh'
    And request { refreshToken: '#(refreshToken)' }
    When method POST
    Then status 401
    And match response.code == 'TOKEN_REVOKED'
```

### 1.3 사용자 정보

```gherkin
Feature: 사용자 정보 관리

  Scenario: 내 정보 조회
    Given 로그인한 구매자
    And path '/users/me'
    And header Authorization = 'Bearer ' + accessToken
    When method GET
    Then status 200
    And match response.email == 'buyer@example.com'
    And match response.name == '구매자'
    And match response.role == 'BUYER'

  Scenario: 내 정보 수정
    Given 로그인한 사용자
    And path '/users/me'
    And header Authorization = 'Bearer ' + accessToken
    And request { name: '새이름', phone: '010-1234-5678' }
    When method PUT
    Then status 200
    And match response.name == '새이름'

  Scenario: 비밀번호 변경
    Given 로그인한 사용자
    And path '/users/me/password'
    And header Authorization = 'Bearer ' + accessToken
    And request { currentPassword: 'Password1!', newPassword: 'NewPassword1!' }
    When method PUT
    Then status 204

    # 새 비밀번호로 로그인
    Given path '/auth/login'
    And request { email: 'buyer@example.com', password: 'NewPassword1!' }
    When method POST
    Then status 200
```

---

## 2. 상품 관리

### 2.1 상품 생성 (판매자)

```gherkin
Feature: 상품 관리

  Scenario: 판매자가 상품 생성
    Given 로그인한 판매자
    And path '/products'
    And header Authorization = 'Bearer ' + sellerToken
    And request
      """
      {
        "name": "아이폰 15",
        "description": "최신 아이폰",
        "price": 1500000,
        "stock": 100,
        "category": "전자제품"
      }
      """
    When method POST
    Then status 201
    And match response.id == '#number'
    And match response.sellerId == '#(sellerId)'

  Scenario: 가격 0 이하로 상품 생성 실패
    Given 로그인한 판매자
    And path '/products'
    And request { name: '상품', price: 0, stock: 10 }
    When method POST
    Then status 400
    And match response.code == 'INVALID_PRICE'

  Scenario: 재고 음수로 상품 생성 실패
    Given 로그인한 판매자
    And path '/products'
    And request { name: '상품', price: 1000, stock: -1 }
    When method POST
    Then status 400
    And match response.code == 'INVALID_STOCK'
```

### 2.2 상품 조회

```gherkin
Feature: 상품 조회

  Scenario: 상품 목록 조회 (비로그인 가능)
    Given path '/products'
    When method GET
    Then status 200
    And match response == '#array'
    And match each response contains { id: '#number', name: '#string', price: '#number' }

  Scenario: 상품 상세 조회
    Given path '/products/1'
    When method GET
    Then status 200
    And match response.id == 1
    And match response.name == '#string'
    And match response.price == '#number'
    And match response.stock == '#number'
    And match response.sellerId == '#number'
    And match response.sellerName == '#string'

  Scenario: 존재하지 않는 상품 조회
    Given path '/products/99999'
    When method GET
    Then status 404
    And match response.code == 'PRODUCT_NOT_FOUND'

  Scenario: 카테고리별 상품 조회
    Given path '/products'
    And param category = '전자제품'
    When method GET
    Then status 200
    And match each response contains { category: '전자제품' }

  Scenario: 판매자 본인 상품 목록 조회
    Given 로그인한 판매자
    And path '/products/mine'
    And header Authorization = 'Bearer ' + sellerToken
    When method GET
    Then status 200
    And match each response contains { sellerId: '#(sellerId)' }
```

### 2.3 상품 수정/삭제

```gherkin
Feature: 상품 수정/삭제

  Scenario: 판매자가 본인 상품 수정
    Given 로그인한 판매자 (상품 소유자)
    And path '/products/' + productId
    And header Authorization = 'Bearer ' + sellerToken
    And request { name: '아이폰 15 Pro', price: 1800000 }
    When method PUT
    Then status 200
    And match response.name == '아이폰 15 Pro'

  Scenario: 다른 판매자 상품 수정 시도
    Given 로그인한 판매자 B
    And 판매자 A의 상품
    And path '/products/' + productAId
    And header Authorization = 'Bearer ' + sellerBToken
    And request { name: '변경시도' }
    When method PUT
    Then status 403
    And match response.code == 'FORBIDDEN'

  Scenario: 판매자가 본인 상품 삭제
    Given 로그인한 판매자 (상품 소유자)
    And path '/products/' + productId
    And header Authorization = 'Bearer ' + sellerToken
    When method DELETE
    Then status 204

    # 삭제 확인
    Given path '/products/' + productId
    When method GET
    Then status 404

  Scenario: 주문이 존재하는 상품 삭제
    Given 상품에 대한 주문이 존재
    And path '/products/' + productId
    And header Authorization = 'Bearer ' + sellerToken
    When method DELETE
    Then status 400
    And match response.code == 'PRODUCT_HAS_ORDERS'
```

---

## 3. 주문 플로우

### 3.1 주문 생성

```gherkin
Feature: 주문 생성

  Background:
    * def product = { id: 1, name: '테스트상품', price: 10000, stock: 100 }

  Scenario: 구매자가 주문 생성
    Given 로그인한 구매자
    And 재고 100개인 상품
    And path '/orders'
    And header Authorization = 'Bearer ' + buyerToken
    And request { productId: '#(productId)', quantity: 3 }
    When method POST
    Then status 201
    And match response.orderId == '#number'
    And match response.status == 'PENDING'
    And match response.totalPrice == 30000  # 10000 * 3

    # 재고 차감 확인
    Given path '/products/' + productId
    When method GET
    Then status 200
    And match response.stock == 97  # 100 - 3

  Scenario: 수량 0 이하로 주문 실패
    Given 로그인한 구매자
    And path '/orders'
    And request { productId: 1, quantity: 0 }
    When method POST
    Then status 400
    And match response.code == 'INVALID_QUANTITY'

  Scenario: 존재하지 않는 상품 주문
    Given 로그인한 구매자
    And path '/orders'
    And request { productId: 99999, quantity: 1 }
    When method POST
    Then status 404
    And match response.code == 'PRODUCT_NOT_FOUND'
```

### 3.2 주문 조회

```gherkin
Feature: 주문 조회

  Scenario: 내 주문 목록 조회
    Given 로그인한 구매자
    And path '/orders'
    And header Authorization = 'Bearer ' + buyerToken
    When method GET
    Then status 200
    And match response == '#array'
    And match each response contains { userId: '#(userId)' }

  Scenario: 주문 상세 조회
    Given 로그인한 구매자 (주문 소유자)
    And path '/orders/' + orderId
    And header Authorization = 'Bearer ' + buyerToken
    When method GET
    Then status 200
    And match response.id == orderId
    And match response.productId == '#number'
    And match response.productName == '#string'
    And match response.quantity == '#number'
    And match response.totalPrice == '#number'
    And match response.status == '#string'
    And match response.createdAt == '#string'

  Scenario: 다른 사용자 주문 조회 시도
    Given 로그인한 구매자 B
    And 구매자 A의 주문
    And path '/orders/' + orderAId
    And header Authorization = 'Bearer ' + buyerBToken
    When method GET
    Then status 403
    And match response.code == 'FORBIDDEN'
```

### 3.3 주문 취소

```gherkin
Feature: 주문 취소

  Scenario: 대기 상태 주문 취소
    Given 로그인한 구매자
    And PENDING 상태의 내 주문
    And 해당 상품 재고가 97개
    And path '/orders/' + orderId + '/cancel'
    And header Authorization = 'Bearer ' + buyerToken
    When method POST
    Then status 204

    # 주문 상태 확인
    Given path '/orders/' + orderId
    And header Authorization = 'Bearer ' + buyerToken
    When method GET
    Then status 200
    And match response.status == 'CANCELLED'

    # 재고 복구 확인
    Given path '/products/' + productId
    When method GET
    Then status 200
    And match response.stock == 100  # 97 + 3 복구

  Scenario: 이미 취소된 주문 재취소 시도
    Given CANCELLED 상태의 주문
    And path '/orders/' + orderId + '/cancel'
    And header Authorization = 'Bearer ' + buyerToken
    When method POST
    Then status 400
    And match response.code == 'ORDER_ALREADY_CANCELLED'

  Scenario: 완료된 주문 취소 시도
    Given COMPLETED 상태의 주문
    And path '/orders/' + orderId + '/cancel'
    And header Authorization = 'Bearer ' + buyerToken
    When method POST
    Then status 400
    And match response.code == 'ORDER_CANNOT_CANCEL'

  Scenario: 다른 사용자 주문 취소 시도
    Given 구매자 A의 주문
    And 로그인한 구매자 B
    And path '/orders/' + orderAId + '/cancel'
    And header Authorization = 'Bearer ' + buyerBToken
    When method POST
    Then status 403
```

### 3.4 주문 상태 변경 (판매자/시스템)

```gherkin
Feature: 주문 상태 관리

  Scenario: 판매자가 주문 확인 (PENDING → CONFIRMED)
    Given 로그인한 판매자 (상품 소유자)
    And PENDING 상태의 주문
    And path '/orders/' + orderId + '/confirm'
    And header Authorization = 'Bearer ' + sellerToken
    When method POST
    Then status 200
    And match response.status == 'CONFIRMED'

  Scenario: 판매자가 배송 시작 (CONFIRMED → SHIPPING)
    Given CONFIRMED 상태의 주문
    And path '/orders/' + orderId + '/ship'
    And header Authorization = 'Bearer ' + sellerToken
    When method POST
    Then status 200
    And match response.status == 'SHIPPING'

  Scenario: 배송 완료 (SHIPPING → DELIVERED)
    Given SHIPPING 상태의 주문
    And path '/orders/' + orderId + '/deliver'
    And header Authorization = 'Bearer ' + sellerToken
    When method POST
    Then status 200
    And match response.status == 'DELIVERED'

  Scenario: 구매 확정 (DELIVERED → COMPLETED)
    Given 로그인한 구매자
    And DELIVERED 상태의 내 주문
    And path '/orders/' + orderId + '/complete'
    And header Authorization = 'Bearer ' + buyerToken
    When method POST
    Then status 200
    And match response.status == 'COMPLETED'
```

---

## 4. 재고 관리

### 4.1 재고 부족

```gherkin
Feature: 재고 관리

  Scenario: 재고보다 많은 수량 주문 시 실패
    Given 재고 5개인 상품
    And 로그인한 구매자
    And path '/orders'
    And request { productId: '#(productId)', quantity: 10 }
    When method POST
    Then status 400
    And match response.code == 'INSUFFICIENT_STOCK'

    # 재고 변화 없음 확인
    Given path '/products/' + productId
    When method GET
    Then status 200
    And match response.stock == 5

  Scenario: 재고 0인 상품 주문 시 실패
    Given 재고 0개인 상품
    And 로그인한 구매자
    And path '/orders'
    And request { productId: '#(productId)', quantity: 1 }
    When method POST
    Then status 400
    And match response.code == 'OUT_OF_STOCK'
```

### 4.2 동시 주문 (동시성)

```gherkin
Feature: 동시 주문 처리

  Scenario: 재고 10개에 동시에 10명이 1개씩 주문
    Given 재고 10개인 상품
    And 10명의 구매자가 동시에 1개씩 주문 요청
    When 모든 요청 처리 완료
    Then 성공한 주문 == 10개
    And 최종 재고 == 0

  Scenario: 재고 5개에 동시에 10명이 1개씩 주문
    Given 재고 5개인 상품
    And 10명의 구매자가 동시에 1개씩 주문 요청
    When 모든 요청 처리 완료
    Then 성공한 주문 == 5개
    And 실패한 주문 == 5개 (INSUFFICIENT_STOCK)
    And 최종 재고 == 0
```

### 4.3 판매자 재고 관리

```gherkin
Feature: 판매자 재고 관리

  Scenario: 판매자가 재고 추가
    Given 로그인한 판매자 (상품 소유자)
    And 현재 재고 50개
    And path '/products/' + productId + '/stock'
    And header Authorization = 'Bearer ' + sellerToken
    And request { amount: 30 }
    When method POST
    Then status 200
    And match response.stock == 80

  Scenario: 판매자가 재고 차감 (수동)
    Given 로그인한 판매자 (상품 소유자)
    And 현재 재고 50개
    And path '/products/' + productId + '/stock'
    And request { amount: -20 }
    When method POST
    Then status 200
    And match response.stock == 30

  Scenario: 재고를 음수로 만드는 차감 실패
    Given 현재 재고 10개
    And path '/products/' + productId + '/stock'
    And request { amount: -20 }
    When method POST
    Then status 400
    And match response.code == 'INSUFFICIENT_STOCK'
```

---

## 5. 권한 & 보안

### 5.1 인증 필요 API

```gherkin
Feature: 인증 필요

  Scenario Outline: 인증 없이 보호된 API 접근
    Given path '<path>'
    When method <method>
    Then status 401
    And match response.code == 'UNAUTHORIZED'

    Examples:
      | path           | method |
      | /orders        | POST   |
      | /orders        | GET    |
      | /orders/1      | GET    |
      | /products      | POST   |
      | /users/me      | GET    |
```

### 5.2 역할 기반 접근 제어

```gherkin
Feature: 역할 기반 권한

  Scenario: 구매자가 상품 생성 시도
    Given 로그인한 구매자 (BUYER)
    And path '/products'
    And header Authorization = 'Bearer ' + buyerToken
    And request { name: '상품', price: 1000, stock: 10 }
    When method POST
    Then status 403
    And match response.code == 'FORBIDDEN'

  Scenario: 판매자가 다른 판매자 상품 수정 시도
    Given 로그인한 판매자 B
    And 판매자 A의 상품
    And path '/products/' + productAId
    And header Authorization = 'Bearer ' + sellerBToken
    When method PUT
    Then status 403

  Scenario: 구매자가 주문 확인(Confirm) 시도
    Given 로그인한 구매자
    And path '/orders/' + orderId + '/confirm'
    And header Authorization = 'Bearer ' + buyerToken
    When method POST
    Then status 403

  Scenario: 관리자 전용 API 접근
    Given 로그인한 관리자 (ADMIN)
    And path '/admin/users'
    And header Authorization = 'Bearer ' + adminToken
    When method GET
    Then status 200
```

### 5.3 리소스 소유권

```gherkin
Feature: 리소스 소유권

  Scenario: 본인 주문만 조회 가능
    Given 구매자 A의 주문
    And 로그인한 구매자 B
    And path '/orders/' + orderAId
    And header Authorization = 'Bearer ' + buyerBToken
    When method GET
    Then status 403

  Scenario: 본인 주문만 취소 가능
    Given 구매자 A의 주문
    And 로그인한 구매자 B
    And path '/orders/' + orderAId + '/cancel'
    And header Authorization = 'Bearer ' + buyerBToken
    When method POST
    Then status 403

  Scenario: 판매자는 본인 상품에 대한 주문만 관리 가능
    Given 판매자 A의 상품에 대한 주문
    And 로그인한 판매자 B
    And path '/orders/' + orderId + '/confirm'
    And header Authorization = 'Bearer ' + sellerBToken
    When method POST
    Then status 403
```

---

## 6. 에러 & 엣지케이스

### 6.1 유효성 검증

```gherkin
Feature: 입력 유효성

  Scenario: 이메일 형식 오류
    Given path '/sign-up/local'
    And request { email: 'invalid-email', password: 'Password1!', name: '테스터' }
    When method POST
    Then status 400
    And match response.code == 'INVALID_EMAIL'

  Scenario: 필수 필드 누락
    Given path '/sign-up/local'
    And request { email: 'test@example.com' }
    When method POST
    Then status 400
    And match response.errors == '#array'
    And match response.errors[*].field contains 'password'
    And match response.errors[*].field contains 'name'

  Scenario: 상품명 길이 초과
    Given 로그인한 판매자
    And path '/products'
    And request { name: '#(256자 이상 문자열)', price: 1000, stock: 10 }
    When method POST
    Then status 400
    And match response.code == 'INVALID_NAME_LENGTH'
```

### 6.2 동시성 & 일관성

```gherkin
Feature: 데이터 일관성

  Scenario: 주문 생성 중 상품 삭제 시도
    Given 상품에 대한 주문 처리 중
    And path '/products/' + productId
    When method DELETE
    Then status 400
    And match response.code == 'PRODUCT_IN_USE'

  Scenario: 주문 취소와 상태 변경 동시 발생
    Given PENDING 상태의 주문
    And 구매자가 취소 요청
    And 동시에 판매자가 확인 요청
    When 두 요청 처리
    Then 하나만 성공하고 하나는 실패
    And 최종 상태는 일관성 있음
```

### 6.3 경계값

```gherkin
Feature: 경계값 테스트

  Scenario: 최대 주문 수량
    Given path '/orders'
    And request { productId: 1, quantity: 1000000 }
    When method POST
    Then status 400
    And match response.code == 'QUANTITY_LIMIT_EXCEEDED'

  Scenario: 최대 가격
    Given path '/products'
    And request { name: '상품', price: 100000000000, stock: 1 }
    When method POST
    Then status 400
    And match response.code == 'PRICE_LIMIT_EXCEEDED'
```

---

## 구현 체크리스트

시나리오별 구현 상태를 여기서 트래킹:

### 1. 사용자 & 인증
- [ ] 1.1 회원가입 (역할 구분 포함)
- [ ] 1.2 로그인/토큰 갱신/로그아웃
- [ ] 1.3 사용자 정보 조회/수정

### 2. 상품 관리
- [ ] 2.1 상품 생성 (판매자만)
- [ ] 2.2 상품 조회 (목록, 상세, 카테고리)
- [ ] 2.3 상품 수정/삭제 (본인 것만)

### 3. 주문 플로우
- [ ] 3.1 주문 생성 + 재고 차감
- [ ] 3.2 주문 조회 (본인 것만)
- [ ] 3.3 주문 취소 + 재고 복구
- [ ] 3.4 주문 상태 변경 (PENDING→CONFIRMED→SHIPPING→DELIVERED→COMPLETED)

### 4. 재고 관리
- [ ] 4.1 재고 부족 시 주문 실패
- [ ] 4.2 동시 주문 동시성 제어
- [ ] 4.3 판매자 재고 수동 관리

### 5. 권한 & 보안
- [ ] 5.1 인증 필요 API 보호
- [ ] 5.2 역할 기반 (BUYER/SELLER/ADMIN)
- [ ] 5.3 리소스 소유권 (내 주문/내 상품)

### 6. 에러 & 엣지케이스
- [ ] 6.1 입력 유효성 검증
- [ ] 6.2 동시성 & 일관성
- [ ] 6.3 경계값 테스트