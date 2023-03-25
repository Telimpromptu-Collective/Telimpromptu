plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.FGWFO"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.javalin:javalin:5.4.2")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("com.beust:klaxon:5.5")
    implementation("com.j2html:j2html:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}