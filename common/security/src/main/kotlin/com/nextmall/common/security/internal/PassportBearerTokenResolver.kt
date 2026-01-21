package com.nextmall.common.security.internal

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import com.nextmall.common.security.internal.SecurityTokenConstants

class PassportBearerTokenResolver : BearerTokenResolver {
    override fun resolve(request: HttpServletRequest): String? {
        val header =
            request.getHeader(SecurityTokenConstants.PASSPORT_HEADER_NAME)
                ?: return null

        return if (header.startsWith(SecurityTokenConstants.BEARER_PREFIX)) {
            header.substring(SecurityTokenConstants.BEARER_PREFIX.length)
        } else {
            null
        }
    }
}
