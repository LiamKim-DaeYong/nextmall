package com.nextmall.userservice.exception

import org.springframework.http.HttpStatus

interface HttpStatusAware {
    val httpStatus: HttpStatus
}
