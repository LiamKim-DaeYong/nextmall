package com.nextmall.common.web.mvc.client

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("nextmall.rest-client")
data class RestClientProperties(
    val connectTimeout: Duration = Duration.ofSeconds(3),
    val readTimeout: Duration = Duration.ofSeconds(5),
) {
    init {
        require(!connectTimeout.isNegative && !connectTimeout.isZero) {
            "connectTimeout must be positive, but was $connectTimeout"
        }
        require(!readTimeout.isNegative && !readTimeout.isZero) {
            "readTimeout must be positive, but was $readTimeout"
        }
    }
}
