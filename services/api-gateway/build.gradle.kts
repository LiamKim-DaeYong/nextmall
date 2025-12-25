plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":common:exception"))
    implementation(libs.spring.cloud.gateway)
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
