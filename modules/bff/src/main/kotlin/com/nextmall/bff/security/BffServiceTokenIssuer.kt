package com.nextmall.bff.security

import com.nextmall.common.security.internal.ServiceTokenIssuer
import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.stereotype.Component

@Component
class BffServiceTokenIssuer(
    serviceTokenProperties: ServiceTokenProperties,
) : ServiceTokenIssuer(serviceTokenProperties) {
    override val serviceName = "bff-service"
}
