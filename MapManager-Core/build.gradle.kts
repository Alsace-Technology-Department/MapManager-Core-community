/*
 * This file was generated by the Gradle 'init' task.
 */
description = "MapManager-Core"

plugins {
    id("buildlogic.java-conventions")
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "work.alsace.mapmanager"
version = "3.1.6"

dependencies {
    implementation(project(":MapManager-API"))
    implementation(project(":v1_16_R1"))
    implementation(project(":v1_20_R1"))

    implementation("net.kyori:adventure-api:4.14.0")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.github.Querz:NBT:6.1")

    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.0")
    compileOnly("net.luckperms:api:5.3")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    from(sourceSets.main.get().output)

    mergeServiceFiles {
        include("META-INF/services/*")
    }

    configurations = listOf(project.configurations.runtimeClasspath.get())
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
