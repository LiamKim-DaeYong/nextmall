plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":common:data"))
    implementation(project(":modules:auth"))
    implementation(project(":modules:user"))
    implementation(project(":modules:bff"))

    implementation(libs.spring.boot.starter.webmvc)
}
