package com.nextmall.orchestrator.security

import com.nextmall.common.web.core.security.passport.SecurityTokenConstants
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class PassportTokenPropagationInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: org.springframework.http.HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val token =
            (authentication as? JwtAuthenticationToken)
                ?.token
                ?.tokenValue
                .orEmpty()
        if (token.isNotEmpty()) {
            request.headers.add(
                SecurityTokenConstants.PASSPORT_HEADER_NAME,
                SecurityTokenConstants.BEARER_PREFIX + token,
            )
        }
        return execution.execute(request, body)
    }
}
