import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val autoServiceKspVersion = "1.2.0"
val autoServiceAnnotationsVersion = "1.1.1"

plugins {
    val kspVersion = "2.3.3"

    kotlin("jvm") version "2.2.21"
    id("com.google.devtools.ksp") version kspVersion
}

group = "de.gianttree.misc"
version = "1.0-SNAPSHOT"

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

tasks.compileKotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_24)
}
