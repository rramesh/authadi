import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"

    // kapt plugin
    id("org.jetbrains.kotlin.kapt") version "1.3.50"

    //flyway migration plugin
    id("org.flywaydb.flyway") version "6.0.6"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.github.com/rramesh/rrproto") {
        credentials {
            username = GithubPackage.user()
            password = GithubPackage.key()
        }
    }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Google Dagger 2 Dependency Injection
    compile("com.google.dagger", "dagger", "2.4")

    // netty to run gRPC
    compile("io.grpc", "grpc-netty-shaded", "1.25.0")
    // Database -HikariCP, PostgreSQL, JDBI with SQLObjects, FlywayDB Migration
    compile("com.zaxxer", "HikariCP", "3.4.1")
    compile("org.postgresql", "postgresql", "42.2.8")
    compile("org.jdbi", "jdbi3-core", "3.10.1")
    compile("org.jdbi", "jdbi3-kotlin", "3.10.1")
    compile("org.jdbi", "jdbi3-postgres", "3.10.1")
    compile("org.jdbi", "jdbi3-sqlobject", "3.10.1")
    compile("org.flywaydb", "flyway-core", "6.0.6")
    // jwt
    compile("io.jsonwebtoken", "jjwt-api", "0.10.7")
    implementation("io.jsonwebtoken:jjwt-impl:0.10.7")
    implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
    // Log4J
    compile("org.apache.logging.log4j", "log4j-slf4j-impl", "2.12.1")
    // proto implementation - Local package, requires ability to pull jar through
    // maven local from https://maven.pkg.github.com/rramesh/rrproto
    implementation("com.rr", "proto", "1.0.2-p02")

    // Test - JUnit 5, Mockk
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("org.flywaydb", "flyway-core", "6.0.6")
    testCompile("com.google.guava", "guava", "28.1-jre")
    testCompile("com.google.dagger", "dagger", "2.4")
    testCompile("io.grpc", "grpc-testing", "1.25.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("io.mockk:mockk:1.9.3")

    kapt("com.google.dagger:dagger-compiler:2.4")
}

application {
    // Define the main class for the application
    mainClassName = "com.rr.authadi.ServiceKt"
}

tasks.withType<Test> {
    systemProperty("authadi.env", "test")
    systemProperty("authadi.propertyFile", System.getProperty("authadi.propertyFile"))
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

object GithubPackage {
    private val ghProperties by lazy { load() }
    private fun load(): Properties {
        return try {
            loadProperties(".protopkg.properties")
        } catch (ex: Exception) {
            val sysP = Properties()
            val ghUser = System.getProperty("github.user") ?: ""
            val ghPassword = System.getProperty("github.key") ?: ""
            sysP.setProperty("github.user", ghUser)
            sysP.setProperty("github.key", ghPassword)
            sysP
        }
    }

    fun user(): String? {
        return ghProperties.getProperty("github.user")
    }

    fun key(): String? {
        return ghProperties.getProperty("github.key")
    }
}

