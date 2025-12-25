package com.nextmall.authservice.exception

import org.springframework.http.HttpStatus

interface HttpStatusAware {
    val httpStatus: HttpStatus
}
