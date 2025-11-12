import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sonarqube)
    base
    jacoco
}

ktlint {
    ignoreFailures.set(false)
    filter {
        exclude("**/build/**")
        exclude("**/generated/**")
    }
}

allprojects {
    group = "com.nextmall"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
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

        property(
            "sonar.exclusions",
            """
            **/build/**,
            **/docker/**,
            **/docs/**,
            **/*.gradle.kts
            """.trimIndent(),
        )

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

    tasks.withType<JacocoReport>().configureEach {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude(
                            "**/ApiApplication*",
                            "**/dto/**",
                            "**/mapper/**",
                            "**/config/**",
                            "**/generated/**",
                        )
                    }
                },
            ),
        )

        reports {
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
        }
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    description = "Generates a unified JaCoCo test coverage report for all subprojects."
    group = "verification"

    val testableProjects = subprojects.filterNot { it.name in listOf("docker", "docs") }

    val testTasks = testableProjects.mapNotNull { it.tasks.findByName("test") }
    val reportTasks = testableProjects.mapNotNull { it.tasks.findByName("jacocoTestReport") }

    dependsOn(testTasks)
    mustRunAfter(reportTasks)

    executionData.from(
        testableProjects.map {
            fileTree(it.layout.buildDirectory) {
                include("jacoco/test.exec")
                include("jacoco/*.exec")
            }
        },
    )

    val allClassDirs =
        testableProjects.flatMap { subproject ->
            val classesDir = subproject.file("build/classes/kotlin/main")
            if (classesDir.exists()) {
                listOf(
                    fileTree(classesDir) {
                        exclude(
                            "**/ApiApplication*",
                            "**/dto/**",
                            "**/mapper/**",
                            "**/config/**",
                            "**/generated/**",
                        )
                    },
                )
            } else {
                emptyList()
            }
        }
    classDirectories.setFrom(allClassDirs)

    val allSourceDirs =
        testableProjects.flatMap { subproject ->
            val srcDir = subproject.file("src/main/kotlin")
            if (srcDir.exists()) listOf(srcDir) else emptyList()
        }
    sourceDirectories.setFrom(files(allSourceDirs))

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoRootReport/jacocoRootReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoRootReport/html"))
    }

    doLast {
        val xmlFile =
            reports.xml.outputLocation
                .get()
                .asFile
        if (!xmlFile.exists() || xmlFile.length() == 0L) {
            throw GradleException("JaCoCo root report generation failed: ${xmlFile.absolutePath} not found or empty.")
        }
        println("JaCoCo unified report generated at: ${xmlFile.absolutePath}")
    }
}

tasks.named("sonar") {
    dependsOn("jacocoRootReport")
}

tasks.named("clean") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("clean") })
}
