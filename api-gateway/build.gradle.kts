plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":common:data"))
    implementation(project(":common:exception"))

    implementation(project(":modules:auth"))
    implementation(project(":modules:bff"))
    implementation(project(":modules:user"))

    implementation(libs.spring.boot.starter.webmvc)
}
