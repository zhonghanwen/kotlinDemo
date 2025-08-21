plugins {
    // For build.gradle.kts (Kotlin DSL)
    kotlin("jvm") version "2.2.10"

    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

}



kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("CoroutinesKt")
}

