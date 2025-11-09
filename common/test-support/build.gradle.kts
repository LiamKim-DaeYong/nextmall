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
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.context)

    implementation(libs.spring.boot.starter.test)
    implementation(libs.kotest.runner.junit5)
    implementation(libs.kotest.assertions.core)
    implementation(libs.kotest.framework.engine)
    implementation(libs.kotest.extensions.spring)
    implementation(libs.mockk)
    implementation(libs.h2)
}
