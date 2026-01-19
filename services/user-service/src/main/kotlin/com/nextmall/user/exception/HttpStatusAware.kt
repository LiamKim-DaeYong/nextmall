package com.nextmall.user.exception

import org.springframework.http.HttpStatus

interface HttpStatusAware {
    val httpStatus: HttpStatus
}
