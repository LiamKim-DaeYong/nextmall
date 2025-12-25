package com.nextmall.auth.domain.account

enum class AuthProvider(
    val providerId: String,
) {
    LOCAL("local"),
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    APPLE("apple"),
}
