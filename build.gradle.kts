import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    repositories {
        maven {
            url = uri("https://repo.starfarm.fun/private")
            credentials {
                username = System.getenv("SF_REPO_USER")
                password = System.getenv("SF_REPO_PASSWORD")
            }
        }
    }
}

plugins {
    id("java")
    kotlin("jvm") version "1.7.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

group = "ru.remsoftware"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/")
    maven {
        url = uri("https://repo.starfarm.fun/private")
        credentials {
            username = System.getenv("SF_REPO_USER")
            password = System.getenv("SF_REPO_PASSWORD")
        }
    }
}
val koraVersion = "0.12.0"


repositories.addAll(buildscript.repositories)
dependencies {
    val kora = platform("ru.tinkoff.kora:kora-parent:$koraVersion")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.12.2-R0.1-SNAPSHOT")
    compileOnly("ru.starfarm:core:1.3.75")
    implementation("mysql:mysql-connector-java:8.0.28")

    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.10-1.0.9")
    implementation(kora)
    ksp(kora)
    ksp("ru.tinkoff.kora:symbol-processors")
    implementation("ru.tinkoff.kora:database-jdbc:0.11.9")
    implementation("ru.tinkoff.kora:common:0.11.9")
    runtimeOnly("ru.tinkoff.kora:config-common:0.11.9")

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
tasks {
    jar {
        archiveFileName.set("KitPvP.jar")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        configurations.runtimeClasspath.get().files.forEach { from(zipTree(it)) }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    withType<Zip> {
        configureEach {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}


