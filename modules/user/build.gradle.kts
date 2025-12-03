import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
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
    implementation(project(":common:util"))
    implementation(project(":common:data"))
    implementation(project(":common:identifier"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)

    testImplementation(project(":common:test-support"))
}
