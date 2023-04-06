import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    application


    id("com.github.node-gradle.node") version "3.5.1"
}

group = "org.FGWFO"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":frontend"))

    implementation("io.javalin:javalin:5.4.2")
    implementation("io.javalin:javalin-rendering:5.4.2")
    implementation("gg.jte:jte:2.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.j2html:j2html:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}


application {
    mainClass.set("MainKt")
}