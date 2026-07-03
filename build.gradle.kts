import org.gradle.jvm.tasks.Jar

plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    register<Jar>("deployJar") {
        dependsOn(named("classes"))
        from(sourceSets.main.get().output)
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        destinationDirectory.set(layout.projectDirectory)
    }

    named("assemble") {
        finalizedBy(named("deployJar"))
    }
}
