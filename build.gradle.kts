import org.jooq.meta.kotlin.forcedType

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.8"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "10.2.1"
}

group = "com.quick"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")

    @Suppress("AvoidDuplicateDependencies")
    runtimeOnly("com.mysql:mysql-connector-j")
    @Suppress("AvoidDuplicateDependencies")
    jooqGenerator("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-mysql:13.5.0")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("com.github.vertical-blank:sql-formatter:2.0.5")

    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val schema: String
)

fun loadDatabaseConfig(): DatabaseConfig {
    val activeProfile = System.getenv("SPRING_PROFILES_ACTIVE")
        ?: project.findProperty("profile")?.toString()
        ?: "dev"

    val envFile = when (activeProfile) {
        "test" -> file(".env.test")
        else -> file(".env")
    }

    val schema = when (activeProfile) {
        "test" -> "quick-chat-test"
        else -> "quick-chat"
    }

    var url = ""
    var user = ""
    var password = ""

    if (envFile.exists()) {
        envFile.useLines { lines ->
            lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
                .forEach { line ->
                    val parts = line.split("=", limit = 2)
                    val key = parts[0].trim()
                    val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")

                    when (key) {
                        "DB_URL" -> url = value
                        "DB_USERNAME" -> user = value
                        "DB_PASSWORD" -> password = value
                    }
                }
        }
    }

    url = System.getenv("DB_URL") ?: url
    user = System.getenv("DB_USERNAME") ?: user
    password = System.getenv("DB_PASSWORD") ?: password

    return DatabaseConfig(url, user, password, schema)
}

val (dbUrl, dbUser, dbPassword, schema) = loadDatabaseConfig()

jooq {
    configurations {
        create("main") {
            version.set("3.19.37")

            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration {
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = schema
                        includes = ".*"
                        excludes = "flyway_schema_history"
                        forcedTypes.apply {
                            forcedType {
                                userType = "java.time.Instant"
                                includeTypes = "(?i:TIMESTAMP)"
                            }
                        }
                    }

                    target.apply {
                        packageName = "com.example.jooq"
                        directory = "build/generated-src/jooq/main"
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                }
            }
        }
    }
}

sourceSets {
    main {
        kotlin.srcDir("build/generated-src/jooq/main")
    }
}

tasks.jar {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}
