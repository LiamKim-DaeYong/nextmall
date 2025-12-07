package com.nextmall.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.process.ExecOperations
import java.net.URI
import javax.inject.Inject

class JooqCodegenPlugin @Inject constructor(
    private val execOperations: ExecOperations
) : Plugin<Project> {

    override fun apply(project: Project) {
        // 1) Load external properties
        val cfg = loadCodegenProperties(project)
        project.extensions.add("codegenConfig", cfg)

        log("NEXTMALL JOOQ Codegen Plugin Loaded")

        // 2) Setup Liquibase classpath (using version catalog)
        val liquibaseClasspath = setupLiquibaseClasspath(project)

        // 3) Register tasks
        registerStartDbTask(project, cfg)
        registerLiquibaseTask(project, liquibaseClasspath, cfg)
        registerStopDbTask(project)

        // 4) Wire jOOQ pipeline
        wireJooqPipeline(project)
    }

    // ----------------------------------------------------------
    // Load config
    // ----------------------------------------------------------
    private fun loadCodegenProperties(project: Project): CodegenConfig {
        val isCi = System.getenv("CI") == "true"
        val file = project.rootProject.file("nextmall-codegen.properties")

        if (!file.exists()) {
            if (isCi) {
                log("⚠ nextmall-codegen.properties not found — CI mode detected, skipping codegen setup")
                return CodegenConfig().apply {
                    url = ""
                    user = ""
                    password = ""
                    schema = ""
                    driver = ""
                }
            } else {
                throw IllegalArgumentException("❌ nextmall-codegen.properties not found at project root!")
            }
        }

        val props = java.util.Properties().apply {
            file.inputStream().use { load(it) }
        }

        return CodegenConfig().apply {
            url = requireNotNull(props["db.url"]) { "db.url is required in nextmall-codegen.properties" }.toString()
            user = requireNotNull(props["db.user"]) { "db.user is required in nextmall-codegen.properties" }.toString()
            password = requireNotNull(props["db.password"]) { "db.password is required in nextmall-codegen.properties" }.toString()
            schema = requireNotNull(props["db.schema"]) { "db.schema is required in nextmall-codegen.properties" }.toString()
            driver = requireNotNull(props["db.driver"]) { "db.driver is required in nextmall-codegen.properties" }.toString()
        }.also {
            log("Loaded codegen properties (schema=${it.schema}, url=${it.url})")
        }
    }

    // ----------------------------------------------------------
    // Liquibase classpath
    // ----------------------------------------------------------
    private fun setupLiquibaseClasspath(project: Project) =
        project.configurations.create("liquibaseClasspath").also { cfg ->
            val libs = project.extensions
                .getByType(VersionCatalogsExtension::class.java)
                .named("libs")

            val liquibaseDep = libs.findLibrary("liquibase-core").get().get()
            val postgresDep = libs.findLibrary("postgresql").get().get()

            project.dependencies.add(cfg.name, liquibaseDep)
            project.dependencies.add(cfg.name, postgresDep)
        }

    // ----------------------------------------------------------
    // Start Docker DB
    // ----------------------------------------------------------
    private fun registerStartDbTask(project: Project, cfg: CodegenConfig) {
        project.tasks.register("startCodegenDb") {
            finalizedBy("stopCodegenDb")

            group = "codegen"
            description = "Starts temporary PostgreSQL container for jOOQ codegen"

            doLast {
                val dbInfo = parseJdbcUrl(cfg.url)

                log("Starting temporary Postgres container...")
                log(" - host=${dbInfo.host}, port=${dbInfo.port}, db=${dbInfo.dbName}, user=${cfg.user}")

                execOperations.exec {
                    commandLine(
                        "docker", "run", "-d",
                        "--name", CONTAINER,
                        "-e", "POSTGRES_USER=${cfg.user}",
                        "-e", "POSTGRES_PASSWORD=${cfg.password}",
                        "-e", "POSTGRES_DB=${dbInfo.dbName}",
                        "-p", "${dbInfo.port}:5432",
                        "postgres:16"
                    )
                }

                waitForPostgres(cfg)
                log("Postgres container '$CONTAINER' is ready")
            }
        }
    }

    // ----------------------------------------------------------
    // Apply Liquibase changelogs
    // ----------------------------------------------------------
    private fun registerLiquibaseTask(
        project: Project,
        liquibaseClasspath: Any,
        cfg: CodegenConfig
    ) {
        project.tasks.register("liquibaseUpdateCodegen", JavaExec::class.java) {
            group = "codegen"
            description = "Applies Liquibase changelog to temporary DB"

            dependsOn("startCodegenDb")
            finalizedBy("stopCodegenDb")

            classpath = project.files(
                project.layout.projectDirectory.dir("src/main/resources"),
                liquibaseClasspath
            )

            mainClass.set("liquibase.integration.commandline.Main")

            args = listOf(
                "--url=${cfg.url}",
                "--username=${cfg.user}",
                "--password=${cfg.password}",
                "--changeLogFile=db/changelog/db.changelog-master.yaml",
                "update"
            )
        }
    }

    // ----------------------------------------------------------
    // Stop Docker DB
    // ----------------------------------------------------------
    private fun registerStopDbTask(project: Project) {
        project.tasks.register("stopCodegenDb") {
            group = "codegen"
            description = "Stops temporary PostgreSQL container"

            doLast {
                log("Stopping temporary Postgres container...")
                try {
                    execOperations.exec {
                        commandLine("docker", "rm", "-f", CONTAINER)
                    }
                    log("Postgres container '$CONTAINER' removed")
                } catch (e: Exception) {
                    log("Container '$CONTAINER' could not be removed (maybe already stopped): ${e.message}")
                }
            }
        }
    }

    // ----------------------------------------------------------
    // Wire generateJooq → Liquibase → Docker pipeline
    // ----------------------------------------------------------
    private fun wireJooqPipeline(project: Project) {
        project.gradle.projectsEvaluated {
            val gen = project.tasks.findByName("generateJooq")

            if (gen != null) {
                gen.dependsOn("liquibaseUpdateCodegen")
                gen.finalizedBy("stopCodegenDb")

                log("generateJooq is wired to Liquibase + Docker pipeline")
            } else {
                log("⚠ generateJooq task not found")
            }
        }
    }

    // ----------------------------------------------------------
    // JDBC URL parsing helpers
    // ----------------------------------------------------------
    private fun parseJdbcUrl(jdbcUrl: String): DbInfo {
        val withoutPrefix = jdbcUrl.removePrefix("jdbc:")
        val mainPart = withoutPrefix.substringBefore("?")

        val uri = URI(mainPart)

        return DbInfo(
            host = uri.host ?: "localhost",
            port = if (uri.port > 0) uri.port else 5432,
            dbName = uri.path.removePrefix("/")
        )
    }

    private fun waitForPostgres(cfg: CodegenConfig) {
        val maxAttempts = 30
        val delay = 1000L

        Class.forName(cfg.driver)

        repeat(maxAttempts) { attempt ->
            try {
                val conn = java.sql.DriverManager.getConnection(cfg.url, cfg.user, cfg.password)
                conn.use {
                    conn.createStatement().use { stmt ->
                        // language=PostgreSQL
                        stmt.executeQuery("SELECT 1")
                    }
                }
                log("Postgres is fully ready (attempt ${attempt + 1})")
                return
            } catch (e: Exception) {
                log("Attempt ${attempt+1} failed: ${e.message}")
                Thread.sleep(delay)
            }
        }

        throw IllegalStateException("Postgres did not become ready: ${cfg.url}")
    }

    data class DbInfo(
        val host: String,
        val port: Int,
        val dbName: String
    )

    companion object {
        private const val CONTAINER = "nextmall-codegen-db"

        fun log(msg: String) {
            println("[NextMall-Codegen] $msg")
        }
    }
}
