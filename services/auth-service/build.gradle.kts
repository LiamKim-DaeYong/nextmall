import com.nextmall.build.ServiceConfig

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jib)
}

dependencies {
    implementation(project(":modules:auth"))

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)

    testImplementation(project(":common:test-support"))
}

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = ServiceConfig.Images.AUTH_SERVICE
        tags = setOf("latest")
    }
    container {
        ports = listOf(ServiceConfig.Ports.AUTH_SERVICE.toString())
        jvmFlags = listOf("-Xms256m", "-Xmx512m")
    }
}
