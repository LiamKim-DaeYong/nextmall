import com.nextmall.build.ServiceConfig

plugins {
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jib)
}

dependencies {
    // common modules
    implementation(project(":common:exception"))
    implementation(project(":common:security"))
    implementation(project(":common:data"))
    implementation(project(":common:identifier"))
    implementation(project(":common:kafka"))
    implementation(project(":common:redis"))
    implementation(project(":common:util"))

    // spring boot
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)

    // database
    runtimeOnly(libs.postgresql)

    // test
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
