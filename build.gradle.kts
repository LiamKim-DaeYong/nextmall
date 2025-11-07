import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.sonarqube) apply true
    alias(libs.plugins.ktlint)
    jacoco
}

allprojects {
    group = "com.nextmall"
    version = "0.0.1-SNAPSHOT"
    repositories { mavenCentral() }
}

subprojects {
    if (project.name !in listOf("docker", "docs")) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
    }
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(false)
        }
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    description = "Generates a unified JaCoCo test coverage report for all subprojects."
    group = "verification"

    dependsOn(subprojects.map { it.tasks.named("test") })

    reports {
        xml.required.set(true)
        html.required.set(false)
    }

    val executionData =
        fileTree(rootDir) {
            include("**/build/jacoco/*.exec")
        }
    executionData(executionData)

    subprojects.forEach { subproject ->
        val sourceSets = subproject.extensions.findByName("sourceSets") as? SourceSetContainer
        sourceSets?.findByName("main")?.let { main ->
            sourceDirectories.from(main.allSource.srcDirs)
            classDirectories.from(main.output)
        }
    }
}
