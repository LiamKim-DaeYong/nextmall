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
    api(libs.jjwt.api)

    implementation(project(":common:exception"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)

    testImplementation(project(":common:test-support"))
}
