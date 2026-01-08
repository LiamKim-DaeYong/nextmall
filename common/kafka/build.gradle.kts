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
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.slf4j.api)

    testImplementation(project(":common:test-support"))
}
