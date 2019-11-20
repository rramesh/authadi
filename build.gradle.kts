import com.google.protobuf.gradle.*

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"

    // kapt plugin
    id("org.jetbrains.kotlin.kapt") version "1.3.50"

    //flyway migration plugin
    id("org.flywaydb.flyway") version "6.0.6"

    // Google protobuf
    id("com.google.protobuf") version "0.8.10"

    //idea plugin
    idea

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Kotlin coroutines
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.2")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Google Dagger 2 Dependency Injection
    compile("com.google.dagger", "dagger", "2.4")

    // GRPC, Protobuf
    compile("com.google.protobuf", "protobuf-java", "3.10.0")
    compile("io.grpc", "grpc-protobuf", "1.15.1")
    compile("io.grpc", "grpc-stub", "1.15.1")

    // Database -HikariCP, PostgreSQL, JDBI with SQLObjects, FlywayDB Migration
    compile("com.zaxxer", "HikariCP", "3.4.1")
    compile("org.postgresql", "postgresql", "42.2.8")
    compile("org.jdbi", "jdbi3-core", "3.10.1")
    compile("org.jdbi", "jdbi3-kotlin", "3.10.1")
    compile("org.jdbi", "jdbi3-postgres", "3.10.1")
    compile("org.jdbi", "jdbi3-sqlobject", "3.10.1")
    compile("org.flywaydb", "flyway-core", "6.0.6")

//  JWT
    compile("io.jsonwebtoken", "jjwt-api", "0.10.7")
    implementation("io.jsonwebtoken:jjwt-impl:0.10.7")
    implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")

//  Result - Railway Oriented Programming
    compile("com.github.kittinunf.result", "result", "2.2.0")
    compile("com.github.kittinunf.result", "result-coroutines", "2.2.0")

//  Log4J
    compile("org.apache.logging.log4j","log4j-core", "2.12.1")
    compile("org.apache.logging.log4j","log4j-slf4j-impl", "2.12.1")

//  Protobuf, GRPC
//    compile(project("proto"))

//  Test - JUnit 5, Mockk
    testCompile("org.flywaydb", "flyway-core", "6.0.6")
    testCompile("com.google.guava", "guava", "28.1-jre")
    testCompile("com.google.dagger", "dagger", "2.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("io.mockk:mockk:1.9.3")
    kapt("com.google.dagger:dagger-compiler:2.4")
}

application {
    // Define the main class for the application
    mainClassName = "com.rr.authadi.ServiceKt"
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.10.0"}
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.25.0"
        }
        id("grpckotlin") {
            artifact = "io.rouz:grpc-kotlin-gen:0.1.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckotlin")
            }
        }
    }
}

idea {
    module {
        sourceDirs = sourceDirs + file("${buildDir}/generated/source/proto/main/java")
        sourceDirs = sourceDirs + file("${buildDir}/generated/source/proto/main/grpc")
        sourceDirs = sourceDirs + file("${buildDir}/generated/source/proto/main/grpckotlin")
    }
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
