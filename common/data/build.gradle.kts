import com.nextmall.build.CodegenConfig

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jooq.plugin)

    id("nextmall.jooq-codegen")
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(project(":common:util"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.liquibase)

    jooqGenerator(libs.postgresql)

    testImplementation("com.h2database:h2")
    testImplementation(project(":common:test-support"))
}

jooq {
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                val cfg = project.extensions.getByType(CodegenConfig::class.java)

                jdbc.apply {
                    driver = cfg.driver
                    url = cfg.url
                    user = cfg.user
                    password = cfg.password
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = cfg.schema
                    }

                    generate.apply {
                        isPojos = false
                        isImmutablePojos = false
                        isDaos = false
                    }

                    target.apply {
                        packageName = "com.nextmall.jooq"
                        directory =
                            project.projectDir
                                .resolve("src/main/jooq")
                                .absolutePath
                    }
                }
            }
        }
    }
}
