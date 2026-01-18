import com.nextmall.build.ServiceConfig

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jib)
}

dependencies {
    implementation(project(":common:exception"))
    implementation(project(":common:security"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.cloud.gateway)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
}

dependencyManagement {
    imports {
        mavenBom(
            libs.spring.cloud.bom
                .get()
                .toString(),
        )
    }
}

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = ServiceConfig.Images.API_GATEWAY
        tags = setOf("latest")
    }
    container {
        ports = listOf(ServiceConfig.Ports.API_GATEWAY.toString())
        jvmFlags = listOf("-Xms256m", "-Xmx512m")
    }
}
