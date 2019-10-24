import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

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
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
//    mavenLocal()
//    mavenCentral()
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

    testCompile("org.flywaydb", "flyway-core", "6.0.6")
    testCompile("com.google.guava", "guava", "28.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

application {
    // Define the main class for the application
    mainClassName = "com.rr.authadi.AppKt"
}

tasks.withType<Test> {
    systemProperty("authadi.propertyFile", System.getProperty("authadi.propertyFile"))
    useJUnitPlatform()
}
