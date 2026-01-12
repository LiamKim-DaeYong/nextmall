plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":common:authorization"))
    implementation(project(":common:exception"))
    implementation(project(":common:security"))
    implementation(project(":common:util"))
    implementation(project(":modules:product"))

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson.module.kotlin)

    testImplementation(project(":common:test-support"))
}
