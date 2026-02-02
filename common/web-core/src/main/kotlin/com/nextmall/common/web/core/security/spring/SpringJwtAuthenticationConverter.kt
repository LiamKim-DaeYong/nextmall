package com.nextmall.common.web.core.security.spring

import com.nextmall.common.web.core.security.principal.AuthenticatedPrincipal
import com.nextmall.common.web.core.security.principal.AuthenticationTokenToPrincipalConverter
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class SpringJwtAuthenticationConverter(
    private val principalConverter: AuthenticationTokenToPrincipalConverter<Jwt>,
) : Converter<Jwt, AbstractAuthenticationToken> {
    private val authoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val principal: AuthenticatedPrincipal = principalConverter.convert(jwt)
        val authorities = authoritiesConverter.convert(jwt)

        return JwtAuthenticationToken(jwt, authorities, principal.userId).apply {
            details = principal
        }
    }
}
