package com.nextmall.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "token.external")
class ExternalTokenProperties(
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
    val privateKey: String,
    val publicKey: String,
    val keyId: String,
) {
    init {
        require(accessTokenExpiration > 0) {
            "accessTokenExpiration must be positive"
        }
        require(refreshTokenExpiration > 0) {
            "refreshTokenExpiration must be positive"
        }
        require(refreshTokenExpiration > accessTokenExpiration) {
            "refreshTokenExpiration must be greater than accessTokenExpiration"
        }
    }
}
