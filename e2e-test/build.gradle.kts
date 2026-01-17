plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.karate.junit5)
    testImplementation(libs.slf4j.api)
    testRuntimeOnly(libs.logback.classic)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// 기본 test task 비활성화 - E2E는 ./gradlew e2e 또는 ./gradlew :e2e-test:e2eTest로만 실행
tasks.test {
    enabled = false
}

// E2E 전용 테스트 task
tasks.register<Test>("e2eTest") {
    group = "e2e"
    description = "Run E2E tests with Karate"

    // 기본 test task의 설정 상속
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform()

    // Karate 옵션 전달
    systemProperty("karate.options", System.getProperty("karate.options") ?: "")
    systemProperty("karate.env", System.getProperty("karate.env") ?: "e2e")

    // E2E 환경 변수 전달 (기본값: localhost:18080)
    systemProperty("gateway.url", System.getenv("E2E_GATEWAY_URL") ?: "http://localhost:18080")
}
