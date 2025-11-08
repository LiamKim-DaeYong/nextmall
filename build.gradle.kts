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
        property("sonar.projectName", "NextMall")
        property("sonar.projectKey", "LiamKim-DaeYong_nextmall")
        property("sonar.organization", System.getProperty("sonar.organization") ?: "")
        property("sonar.host.url", "https://sonarcloud.io")

        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml",
        )
        property("sonar.exclusions", "**/build/**, **/docker/**, **/docs/**, **/*.gradle.kts")

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

    val testableProjects =
        subprojects.filter {
            it.name !in listOf("docker", "docs")
        }

    val testTasks =
        testableProjects.mapNotNull { subproject ->
            try {
                subproject.tasks.named("test")
            } catch (_: Exception) {
                println("No test task found in ${subproject.name}")
                null
            }
        }

    dependsOn(testTasks)

    reports {
        xml.required.set(true)
        html.required.set(false)
    }

    executionData.from(
        testableProjects.map {
            fileTree(it.layout.buildDirectory) {
                include("jacoco/test.exec")
                include("jacoco/*.exec")
            }
        },
    )

    testableProjects.forEach { subproject ->
        try {
            val sourceSets = subproject.extensions.findByName("sourceSets") as? SourceSetContainer
            sourceSets?.findByName("main")?.let { main ->
                sourceDirectories.from(main.allSource.srcDirs)
                classDirectories.from(main.output.classesDirs)
            }
        } catch (e: Exception) {
            println("Could not configure source sets for ${subproject.name}: ${e.message}")
        }
    }

    doLast {
        val reportFile =
            reports.xml.outputLocation
                .get()
                .asFile
        if (!reportFile.exists() || reportFile.length() == 0L) {
            throw GradleException("JaCoCo report generation failed: $reportFile is missing or empty")
        }
        println("JaCoCo report generated successfully at: $reportFile")
    }
}

tasks.named("sonar") {
    dependsOn("jacocoRootReport")
}
