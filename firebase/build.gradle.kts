plugins {
    kotlin("js") version "1.5.20"
}

group = "tiny.angry.kitten"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
}

kotlin {
    js(IR) {
        this.useCommonJs()
        binaries.executable()
        moduleName = "index"
        nodejs {
            distribution {
                directory = File("functions")
            }
        }

    }
}