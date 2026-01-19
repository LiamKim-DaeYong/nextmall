package com.nextmall.auth.exception

import org.springframework.http.HttpStatus

interface HttpStatusAware {
    val httpStatus: HttpStatus
}
