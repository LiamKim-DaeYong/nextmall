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
    api(project(":common:web-core"))
    api(libs.spring.boot.starter.webflux)
    api(libs.spring.boot.starter.validation)

    implementation(project(":common:exception"))
    api(libs.jackson.module.kotlin)
}
