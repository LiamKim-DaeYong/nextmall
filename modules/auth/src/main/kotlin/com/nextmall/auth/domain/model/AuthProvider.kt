package com.nextmall.auth.domain.model

enum class AuthProvider(
    val providerId: String,
) {
    LOCAL("local"),
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    APPLE("apple"),
}
