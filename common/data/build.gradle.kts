import com.nextmall.build.CodegenConfig

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jooq)

    id("nextmall.jooq-codegen")
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)

    jooqGenerator(libs.postgresql)
}

jooq {
    configurations {
        create("main") {
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
                        isPojos = true
                        isImmutablePojos = true
                        isDaos = false
                    }

                    target.apply {
                        packageName = "com.nextmall.jooq"
                        directory =
                            project.layout.buildDirectory
                                .dir("generated/jooq")
                                .get()
                                .asFile
                                .absolutePath
                    }
                }
            }
        }
    }
}
