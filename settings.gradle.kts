rootProject.name = "nextmall"

include("api")               // 애플리케이션 진입점 (Spring Boot 실행 모듈)
include("modules:auth")      // 인증/인가 도메인
include("modules:user")      // 회원 도메인
include("common:security")   // 공통 보안/암호화 설정 모듈
