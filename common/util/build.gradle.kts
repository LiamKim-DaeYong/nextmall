plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(kotlin("stdlib"))
}
