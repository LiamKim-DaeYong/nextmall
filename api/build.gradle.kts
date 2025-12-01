plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":common:identifier"))
    implementation(project(":common:data"))

    implementation(project(":modules:auth"))
    implementation(project(":modules:user"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    runtimeOnly(libs.postgresql)

    testImplementation(project(":common:test-support"))
}
