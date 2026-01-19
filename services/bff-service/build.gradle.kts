import com.nextmall.build.ServiceConfig

plugins {
    alias(libs.plugins.jib)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":common:exception"))
    implementation(project(":common:integration"))
    implementation(project(":common:security"))
    implementation(project(":common:util"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.webmvc)

    testImplementation(project(":common:test-support"))
}

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = ServiceConfig.Images.BFF_SERVICE
        tags = setOf("latest")
    }
    container {
        ports = listOf(ServiceConfig.Ports.BFF_SERVICE.toString())
        jvmFlags = listOf("-Xms256m", "-Xmx512m")
    }
}
