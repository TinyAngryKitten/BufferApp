plugins {
    kotlin("js") version "1.5.20"
}

group = "tiny.angry.kitten"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    implementation("tiny.angry.kitten:SimpleBankClient:1.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.5.1")
    implementation(npm("firebase-admin", "^9.2.0"))
    implementation(npm("firebase-functions", "^3.11.0"))
    implementation(npm("@js-joda/core", "^3.2.0"))
}

kotlin {
    js(IR) {
        this.useCommonJs()
        binaries.executable()

        moduleName = "index"

        compilations["main"].packageJson {
            customField("scripts",
                mapOf(
                    "serve" to "firebase emulators:start --only functions",
                    "shell" to "firebase functions:shell",
                    "start" to "npm run shell",
                    "deploy" to "firebase deploy --only functions",
                    "logs" to "firebase functions:log"
                )
            )

            //customField("engines", mapOf("node" to "12"))

            private = false
            name = "functions"
            main = "index.js"
        }

        nodejs {

        }

    }
}

tasks.register<Copy>("copyIndexToFunctionsFolder") {
    dependsOn(":build")
    //delete("../functions/index.js")
    from(layout.buildDirectory.file("js/packages/index/kotlin/index.js"))
    into(layout.buildDirectory.dir("../functions"))
}

tasks.register<Copy>("copyPackageJsonToFunctionsFolder") {
    dependsOn(":copyIndexToFunctionsFolder")
    //delete("../functions/package.json")
    from(layout.buildDirectory.file("js/packages/index/package.json"))
    into(layout.buildDirectory.dir("../functions"))
}

tasks.register<Exec>("npmInstall") {
    dependsOn(":copyPackageJsonToFunctionsFolder")
    this.workingDir = layout.buildDirectory.dir("../functions").get().asFile
    if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)) {
        commandLine("npm.cmd", "install")
    } else commandLine("npm", "install")
}

tasks.findByPath(":build")?.finalizedBy(":copyIndexToFunctionsFolder")
tasks.findByPath(":copyIndexToFunctionsFolder")?.finalizedBy(":copyPackageJsonToFunctionsFolder")
tasks.findByPath(":copyPackageJsonToFunctionsFolder")?.finalizedBy(":npmInstall")