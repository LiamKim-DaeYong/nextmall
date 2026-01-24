package com.nextmall.orchestrator.client.auth.response

data class TokenClientResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    override fun toString(): String =
        "TokenClientResponse(accessToken=${mask(accessToken)}, refreshToken=${mask(refreshToken)})"

    private fun mask(value: String): String =
        if (value.isBlank()) "<empty>" else "***"
}
