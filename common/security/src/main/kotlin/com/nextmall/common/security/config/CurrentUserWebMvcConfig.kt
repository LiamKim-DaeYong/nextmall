package com.nextmall.common.security.config

import com.nextmall.common.security.spring.CurrentUserArgumentResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @CurrentUser 어노테이션 지원을 위한 WebMvc 설정.
 *
 * WebMvc(Servlet) 환경에서만 활성화된다.
 * WebFlux 환경(Gateway, BFF)에서는 자동으로 비활성화된다.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class CurrentUserWebMvcConfig : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(CurrentUserArgumentResolver())
    }
}
