import com.github.gradle.node.npm.task.NpxTask

plugins {
    java
    // You have to specify the plugin version, for instance
    // id("com.github.node-gradle.node") version "3.0.0"
    // This works as is here because we use the plugin source
    id("com.github.node-gradle.node") version "3.5.1"
}

/*
val lintTask = tasks.register<NpxTask>("lintWebapp") {
    command.set("npm")
    args.set(listOf("run build"))
    dependsOn(tasks.npmInstall)
    inputs.dir("src")
    inputs.dir("node_modules")
    outputs.upToDateWhen { true }
}

 */

val buildTask = tasks.register<NpxTask>("buildWebapp") {
    command.set("react-scripts")
    args.set(listOf("build"))
    dependsOn(tasks.npmInstall)
    inputs.dir(project.fileTree("src"))
    inputs.dir("node_modules")
    outputs.dir("${buildDir}/webapp")
    environment.set(mapOf("BUILD_PATH" to "${buildDir}/webapp/webroot"))
}

/*
val testTask = tasks.register<NpxTask>("testWebapp") {
    command.set("ng")
    args.set(listOf("test"))
    dependsOn(tasks.npmInstall, lintTask)
    inputs.dir("src")
    inputs.dir("node_modules")
    inputs.files("angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.spec.json", "karma.conf.js")
    outputs.upToDateWhen { true }
}

 */

sourceSets {
    java {
        main {
            resources {
                // This makes the processResources task automatically depend on the buildWebapp one
                srcDir(buildTask)
            }
        }
    }
}
