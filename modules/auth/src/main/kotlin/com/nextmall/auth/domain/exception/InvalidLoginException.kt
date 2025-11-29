package com.nextmall.auth.domain.exception

/**
 * Thrown when a login attempt fails due to invalid credentials.
 */
class InvalidLoginException(
    message: String = "Invalid email or password.",
) : RuntimeException(message)
