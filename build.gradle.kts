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

val coverageExcludes =
    listOf(
        "**/health*",
        "**/*Application*",
        "**/dto/**",
        "**/mapper/**",
        "**/config/**",
        "**/generated/**",
        "**/*Exception*",
        "**/exception/**",
        "**/jooq/**",
    )

val sonarExtraExcludes =
    listOf(
        "**/build/**",
        "**/docker/**",
        "**/docs/**",
        "**/*.gradle.kts",
    )

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
            (coverageExcludes + sonarExtraExcludes).joinToString(","),
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

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        ignoreFailures.set(false)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude("**/jooq/**")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        systemProperty(
            "kotest.framework.config.fqn",
            "com.nextmall.common.testsupport.config.ProjectConfig",
        )
        testLogging {
            events("passed", "skipped", "failed")
        }

        // 테스트용 환경변수 자동 설정
        environment(
            "TOKEN_PASSPORT_SECRET",
            System.getenv("TOKEN_PASSPORT_SECRET")
                ?: "dGVzdC1wYXNzcG9ydC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZw==",
        )

        finalizedBy("jacocoTestReport")
    }

    tasks.withType<JacocoReport>().configureEach {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude(coverageExcludes)
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
    dependsOn(testableProjects.mapNotNull { it.tasks.findByName("test") })

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
                listOf(fileTree(classesDir) { exclude(coverageExcludes) })
            } else {
                emptyList()
            }
        }

    classDirectories.setFrom(allClassDirs)

    val allSourceDirs =
        testableProjects.mapNotNull { subproject ->
            val srcDir = subproject.file("src/main/kotlin")
            if (srcDir.exists()) srcDir else null
        }

    sourceDirectories.setFrom(files(allSourceDirs))

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoRootReport/jacocoRootReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoRootReport/html"))
    }
}

tasks.named("sonar") {
    dependsOn("jacocoRootReport")
}

tasks.named("clean") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("clean") })
    doLast {
        delete("${rootProject.projectDir}/buildSrc/build")
    }
}

// ==================== Docker Image Build Tasks ====================

val allServices =
    listOf("api-gateway", "auth-service", "bff-service", "user-service", "order-service", "product-service")

fun loadDockerImages(services: List<String>) {
    services.forEach { service ->
        val tarFile = file("services/$service/build/jib-image.tar")
        if (tarFile.exists()) {
            val process =
                ProcessBuilder("docker", "load", "-i", tarFile.absolutePath)
                    .inheritIO()
                    .start()
            val completed = process.waitFor(5, TimeUnit.MINUTES)
            if (!completed) {
                process.destroyForcibly()
                throw GradleException("Timeout loading image for $service")
            }
            val exitCode = process.exitValue()
            if (exitCode == 0) {
                println("Loaded image for $service")
            } else {
                throw GradleException("Failed to load image for $service (exit code: $exitCode)")
            }
        }
    }
}

tasks.register("buildAllImages") {
    group = "docker"
    description = "Build tar files for all services and load them into Docker"

    dependsOn(allServices.map { ":services:$it:jibBuildTar" })

    doLast {
        loadDockerImages(allServices)
    }
}

// ==================== E2E Test Tasks ====================

val e2eServices =
    listOf("auth-service", "bff-service", "user-service", "product-service", "order-service", "api-gateway")

tasks.register("e2eBuildImages") {
    group = "e2e"
    description = "Build Docker images for E2E services using tar method"

    dependsOn(e2eServices.map { ":services:$it:jibBuildTar" })

    doLast {
        loadDockerImages(e2eServices)
    }
}

tasks.register<Exec>("e2eUp") {
    group = "e2e"
    description = "Start E2E test environment using Docker Compose"

    workingDir = rootDir
    commandLine("docker", "compose", "-f", "docker/docker-compose.e2e.yml", "up", "-d", "--wait")

    dependsOn("e2eBuildImages")
}

tasks.register<Exec>("e2eDown") {
    group = "e2e"
    description = "Stop E2E test environment"

    workingDir = rootDir
    commandLine("docker", "compose", "-f", "docker/docker-compose.e2e.yml", "down")
}

tasks.register("e2eTest") {
    group = "e2e"
    description = "Run E2E tests (assumes environment is already running)"

    dependsOn(":e2e-test:e2eTest")
}

tasks.register("e2e") {
    group = "e2e"
    description = "Full E2E test cycle: build images → start environment → run tests → stop environment"

    dependsOn("e2eUp", "e2eTest")
    finalizedBy("e2eDown")
}

tasks.named("e2eTest") {
    mustRunAfter("e2eUp")
}

// e2e-test 모듈의 실제 테스트 task도 e2eUp 이후에 실행되도록 설정
gradle.projectsEvaluated {
    project(":e2e-test").tasks.named("e2eTest") {
        mustRunAfter(rootProject.tasks.named("e2eUp"))
    }
}
