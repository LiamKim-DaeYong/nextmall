plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly(libs.spring.boot.starter.webmvc)
}
