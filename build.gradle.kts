import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val autoServiceKspVersion = "1.2.0"
val autoServiceAnnotationsVersion = "1.1.1"

plugins {
    val kspVersion = "2.3.3"

    kotlin("jvm") version "2.3.0"
    id("com.google.devtools.ksp") version kspVersion
}

group = "de.gianttree.misc"
version = "1.0-SNAPSHOT"

val generatedCommonSources: Provider<Directory> = layout.buildDirectory.dir("generated/common")

repositories {
    mavenCentral()
}

dependencies {
    ksp("dev.zacsweers.autoservice:auto-service-ksp:$autoServiceKspVersion")
    implementation("com.google.auto.service:auto-service-annotations:$autoServiceAnnotationsVersion")
    testImplementation(kotlin("test"))
}

ksp {
    arg("autoserviceKsp.verify", "true")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        kotlin {
            srcDir(generatedCommonSources)
        }
    }
}

tasks.withType<KspAATask> {
    dependsOn(tasks.getByName("generateCode"))
}

tasks.compileKotlin {
    dependsOn("generateCode")
    compilerOptions.jvmTarget.set(JvmTarget.JVM_24)
}

tasks.register<Copy>("generateCode") {
    val inputDir = file(layout.projectDirectory.dir("src/main/template/common"))
    from(inputDir)
    destinationDir = file(generatedCommonSources)
    destinationDir.deleteRecursively()
    destinationDir.mkdirs()
    val tree = fileTree(inputDir) {
        include("**/*.kt.template")
    }

    // Replace primitives
    listOf("Boolean", "Byte", "Short", "Char", "Int", "Long", "Float", "Double").map { primitiveType ->
        tree.visit {
            if (this.isDirectory) return@visit

            // Recreate the source file tree
            val outputFile =
                destinationDir.resolve(
                    file.relativeTo(inputDir).resolveSibling(
                        file.name
                            .replace("Primitive", primitiveType)
                            .replace(".template$".toRegex(), "")
                    )
                )

            val outputContent = this.file.readText().replace("#TYPE#", primitiveType)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(outputContent)
        }
    }
}
