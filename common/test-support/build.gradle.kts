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

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.context)

    api(libs.spring.boot.starter.webmvc.test)
    api(libs.spring.boot.starter.data.jpa.test)
    api(libs.kotest.runner.junit5)
    api(libs.kotest.assertions.core)
    api(libs.kotest.framework.engine)
    api(libs.kotest.extensions.spring)
    api(libs.mockk)
    api(libs.springmockk)
    api(libs.h2)
}
