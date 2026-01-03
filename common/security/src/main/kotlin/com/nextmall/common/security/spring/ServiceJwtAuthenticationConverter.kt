package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.ServicePrincipal
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class ServiceJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {
    private val authoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val serviceName =
            jwt.subject
                ?: throw IllegalArgumentException("JWT subject(service name)가 존재하지 않습니다.")

        val scope = jwt.getClaimAsString("scope") ?: ""

        val principal =
            ServicePrincipal(
                serviceName = serviceName,
                scope = scope,
            )

        val authorities = authoritiesConverter.convert(jwt)

        return JwtAuthenticationToken(jwt, authorities, serviceName).apply {
            details = principal
        }
    }
}
