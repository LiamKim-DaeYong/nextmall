package com.nextmall.auth.domain.exception

import com.nextmall.auth.application.exception.AuthErrorCode
import com.nextmall.common.exception.base.BaseException

/**
 * 이미 존재하는 인증 계정을 생성하려 할 때 발생하는 예외.
 *
 * provider와 providerAccountId 조합이 중복될 경우,
 * 데이터베이스의 유니크 제약 조건에 의해 감지되며
 * 해당 상황을 비즈니스 오류로 변환하기 위해 사용된다.
 *
 * 이 예외는 동시성 환경에서도 안전한 처리를 위해
 * 사전 조회가 아닌 DB 제약 위반을 기준으로 발생한다.
 */
class DuplicateAuthAccountException : BaseException(AuthErrorCode.DUPLICATE_ACCOUNT)
