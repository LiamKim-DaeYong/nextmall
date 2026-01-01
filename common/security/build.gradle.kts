plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(project(":common:exception"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    testImplementation(project(":common:test-support"))
}
