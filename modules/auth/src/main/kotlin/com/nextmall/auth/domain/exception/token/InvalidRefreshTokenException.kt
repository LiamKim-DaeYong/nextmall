package com.nextmall.auth.domain.exception.token

/**
 * Thrown when a refresh token is invalid, expired, or does not match the stored value.
 */
class InvalidRefreshTokenException(
    message: String = "Invalid or expired refresh token.",
) : RuntimeException(message)
