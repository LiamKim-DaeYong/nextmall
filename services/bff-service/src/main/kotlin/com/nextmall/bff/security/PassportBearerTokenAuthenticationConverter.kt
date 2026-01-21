package com.nextmall.bff.security

import com.nextmall.common.security.internal.SecurityTokenConstants
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter

class PassportBearerTokenAuthenticationConverter : ServerBearerTokenAuthenticationConverter() {
    init {
        setBearerTokenHeaderName(SecurityTokenConstants.PASSPORT_HEADER_NAME)
    }
}
