plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.postgresql)
    implementation(libs.liquibase.core)
}

gradlePlugin {
    plugins {
        create("nextmallJooqCodegen") {
            id = "nextmall.jooq-codegen"
            implementationClass = "com.nextmall.build.JooqCodegenPlugin"
        }
    }
}
