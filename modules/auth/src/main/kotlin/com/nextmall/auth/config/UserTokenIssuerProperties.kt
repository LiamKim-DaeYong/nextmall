package com.nextmall.auth.config

import com.nextmall.common.security.token.UserTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "token.user")
class UserTokenIssuerProperties(
    override val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
) : UserTokenProperties
