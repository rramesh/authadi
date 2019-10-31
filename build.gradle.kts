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
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    compile("com.google.dagger", "dagger", "2.4")
    compile("com.zaxxer", "HikariCP", "3.4.1")
    compile("org.postgresql", "postgresql", "42.2.8")
    compile("org.jdbi", "jdbi3-core", "3.10.1")
    compile("org.jdbi", "jdbi3-kotlin", "3.10.1")
    compile("org.flywaydb", "flyway-core", "6.0.6")
    compile("org.jdbi", "jdbi3-sqlobject", "3.10.1")
    compile("org.apache.logging.log4j","log4j-core", "2.12.1")
    compile("org.apache.logging.log4j","log4j-slf4j-impl", "2.12.1")

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
