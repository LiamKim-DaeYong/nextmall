plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(project(":common:identifier"))
    implementation(project(":common:redis"))
    implementation(project(":common:security"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.context)
    implementation(libs.kotlin.reflect)

    // Test frameworks
    api(libs.spring.boot.webtestclient)
    api(libs.spring.boot.starter.webmvc.test)
    api(libs.spring.boot.starter.webclient.test)
    api(libs.spring.boot.starter.data.jpa.test)
    api(libs.spring.boot.starter.data.redis.test)
    api(libs.kotest.runner.junit5)
    api(libs.kotest.assertions.core)
    api(libs.kotest.framework.engine)
    api(libs.kotest.extensions.spring)
    api(libs.mockk)
    api(libs.springmockk)

    // Testcontainers
    api(platform(libs.testcontainers.bom))
    api(libs.testcontainers.core)
    api(libs.testcontainers.junit5)
    api(libs.testcontainers.postgresql)
    api(libs.testcontainers.kafka)
    api(libs.postgresql)

    // Kafka test
    api(libs.spring.kafka.test)
}
