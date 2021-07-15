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
        nodejs {}

    }
}

tasks.register<Copy>("copyIndexToFunctionsFolder") {
    dependsOn(":build")
    from(layout.buildDirectory.file("js/packages/index/kotlin/index.js"))
    into(layout.buildDirectory.dir("../functions"))
}

tasks.findByPath(":build")?.finalizedBy(":copyIndexToFunctionsFolder")
