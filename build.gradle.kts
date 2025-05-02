plugins {
    kotlin("jvm") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.cubewhy.celestial"
version = "1.2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.maven:maven-artifact:4.0.0-rc-3")
    compileOnly("org.ow2.asm:asm:9.7.1")

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

//    relocate("org.objectweb.asm", "shadowed.org.objectweb.asm")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}