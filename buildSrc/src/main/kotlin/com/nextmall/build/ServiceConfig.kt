package com.nextmall.build

/**
 * 서비스별 포트 및 설정 정보
 * - gradle 빌드 스크립트와 e2e 테스트에서 공통으로 사용
 * - application.yml의 server.port와 일치해야 함
 */
object ServiceConfig {

    object Ports {
        const val API_GATEWAY = 8080
        const val AUTH_SERVICE = 8081
        const val BFF_SERVICE = 8082
        const val USER_SERVICE = 8083
        const val PRODUCT_SERVICE = 8084
        const val ORDER_SERVICE = 8085
        const val CHECKOUT_SERVICE = 8086
        const val ORCHESTRATOR_SERVICE = 8087
    }

    object Images {
        private const val REGISTRY = "nextmall"

        const val API_GATEWAY = "$REGISTRY/api-gateway"
        const val AUTH_SERVICE = "$REGISTRY/auth-service"
        const val BFF_SERVICE = "$REGISTRY/bff-service"
        const val USER_SERVICE = "$REGISTRY/user-service"
        const val PRODUCT_SERVICE = "$REGISTRY/product-service"
        const val ORDER_SERVICE = "$REGISTRY/order-service"
        const val CHECKOUT_SERVICE = "$REGISTRY/checkout-service"
        const val ORCHESTRATOR_SERVICE = "$REGISTRY/orchestrator-service"
    }
}
