import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jpa)
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
    api(project(":common:exception"))
    api(project(":common:security"))

    implementation(project(":common:data"))
    implementation(project(":common:identifier"))
    implementation(project(":common:kafka"))
    implementation(project(":common:redis"))
    implementation(project(":common:util"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.webmvc)

    runtimeOnly(libs.postgresql)

    testImplementation(project(":common:test-support"))
}
