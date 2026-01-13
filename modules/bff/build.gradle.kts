import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    api(project(":common:security"))
    api(project(":common:util"))

    implementation(project(":common:exception"))
    implementation(project(":common:integration"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.webmvc)

    testImplementation(project(":common:test-support"))
}
