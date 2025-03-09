plugins {
    kotlin("jvm") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.cubewhy.celestial"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")

    testImplementation(kotlin("test"))
}

tasks.jar {
    dependsOn("shadowJar")

    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes(
            "Premain-Class" to "org.cubewhy.celestial.debugger.agent.BooleanModifierAgent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("fatjar")
    archiveVersion.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("native-binaries/**")

    exclude("LICENSE.txt")

    exclude("META-INF/maven/**")
    exclude("META-INF/versions/**")

    exclude("org/junit/**")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}