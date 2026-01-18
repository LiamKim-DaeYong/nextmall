package com.nextmall.authservice.presentation.controller

import com.nextmall.auth.config.ExternalTokenProperties
import com.nextmall.auth.infrastructure.security.JwtTokenProvider
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/.well-known")
class JwksController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val externalTokenProperties: ExternalTokenProperties,
) {
    @GetMapping("/jwks.json", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun jwks(): Map<String, Any> {
        val publicKey = jwtTokenProvider.getPublicKey()

        val jwk =
            RSAKey
                .Builder(publicKey)
                .keyID(externalTokenProperties.keyId)
                .build()

        val jwkSet = JWKSet(jwk)

        return jwkSet.toJSONObject()
    }
}
