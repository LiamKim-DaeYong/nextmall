import com.nextmall.build.ServiceConfig

plugins {
    alias(libs.plugins.jib)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":common:data"))
    implementation(project(":common:exception"))
    implementation(project(":common:identifier"))
    implementation(project(":common:redis"))
    implementation(project(":common:security"))
    implementation(project(":common:util"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.webmvc)

    runtimeOnly(libs.postgresql)

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
