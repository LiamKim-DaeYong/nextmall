package com.nextmall.common.security.internal

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver

class PassportBearerTokenResolver : BearerTokenResolver {
    override fun resolve(request: HttpServletRequest): String? {
        val header =
            request.getHeader(PassportTokenConstants.HEADER_NAME)
                ?: return null

        return if (header.startsWith(TOKEN_PREFIX)) {
            header.substring(TOKEN_PREFIX.length)
        } else {
            null
        }
    }

    companion object {
        private const val TOKEN_PREFIX = "Bearer "
    }
}
