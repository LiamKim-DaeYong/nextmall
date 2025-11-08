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

sonar {
    properties {
        property("sonar.projectName", project.name.replaceFirstChar { it.uppercase() })
        property("sonar.projectKey", System.getProperty("sonar.projectKey") ?: "${project.group}:${project.name}")
        property("sonar.organization", System.getProperty("sonar.organization") ?: "")
        property("sonar.host.url", System.getProperty("sonar.host.url") ?: "https://sonarcloud.io")

        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml")
        property("sonar.exclusions", "**/build/**, **/docker/**, **/docs/**")

        System.getProperty("sonar.branch.name")?.let { branchName ->
            property("sonar.branch.name", branchName)
        }
    }
}

subprojects {
    if (project.name !in listOf("docker", "docs")) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "jacoco")
    }
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

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

    executionData.from(fileTree(rootDir) { include("**/build/jacoco/*.exec") })

    subprojects.forEach { subproject ->
        val sourceSets = subproject.extensions.findByName("sourceSets") as? SourceSetContainer
        sourceSets?.findByName("main")?.let { main ->
            sourceDirectories.from(main.allSource.srcDirs)
            classDirectories.from(main.output)
        }
    }
}

tasks.named("sonar") {
    dependsOn("jacocoRootReport")
}
