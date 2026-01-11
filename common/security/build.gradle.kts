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
    api(libs.spring.boot.starter.oauth2.resource.server)
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.webmvc)
    api(libs.nimbus.jose.jwt)

    implementation(project(":common:exception"))
    implementation(libs.kotlin.reflect)

    testImplementation(project(":common:test-support"))
}
