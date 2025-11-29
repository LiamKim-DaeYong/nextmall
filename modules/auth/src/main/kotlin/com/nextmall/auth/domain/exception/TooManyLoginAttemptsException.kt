package com.nextmall.auth.domain.exception

/**
 * Thrown when a user exceeds the allowed number of login attempts.
 */
class TooManyLoginAttemptsException(
    message: String = "Too many failed login attempts. Please try again later.",
) : RuntimeException(message)
