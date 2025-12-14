plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.spring.boot.starter.webmvc)

    runtimeOnly(project(":common:data"))
    runtimeOnly(project(":modules:auth"))
    runtimeOnly(project(":modules:user"))
    runtimeOnly(project(":modules:bff"))
}
