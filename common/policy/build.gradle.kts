plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}
