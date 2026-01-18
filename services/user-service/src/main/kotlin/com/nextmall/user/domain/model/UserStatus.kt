package com.nextmall.user.domain.model

enum class UserStatus {
    PENDING, // 가입 진행 중 (signup flow 시작)
    ACTIVE, // 정상 사용자
    DORMANT, // 휴면
    WITHDRAWN, // 탈퇴
    SIGNUP_FAILED, // 회원가입 플로우 실패
}
