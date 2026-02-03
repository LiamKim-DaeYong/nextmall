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
    implementation(project(":common:util"))
    implementation(project(":common:web-reactive"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.opentelemetry)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.springdoc.openapi.starter.webflux.ui)

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
