package com.nextmall.apigateway.security.token

import com.nextmall.common.security.internal.PassportTokenIssuer
import com.nextmall.common.security.token.PassportTokenProperties
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class GatewayPassportTokenIssuer(
    passportTokenProperties: PassportTokenProperties,
) : PassportTokenIssuer(passportTokenProperties) {
    override val serviceName: String = "gateway"

    fun issueFromExternalToken(externalToken: Jwt): String {
        val userId =
            externalToken.subject
                ?: throw IllegalArgumentException("External token must contain 'subject' claim")

        require(userId.isNotBlank()) { "External token 'subject' claim must not be blank" }

        val roles = externalToken.getClaimAsStringList("roles")?.toSet() ?: emptySet()

        return issuePassportToken(
            targetService = "internal-services",
            userId = userId,
            roles = roles,
        )
    }

    fun issueForGuest(): String =
        issuePassportToken(
            targetService = "internal-services",
            userId = GUEST_USER_ID,
            roles = emptySet(),
        )

    private companion object {
        private const val GUEST_USER_ID = "guest"
    }
}
