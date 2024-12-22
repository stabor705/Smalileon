plugins {
    kotlin("jvm") version "2.0.21"
    antlr
}

group = "xyz.stabor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("me.tongfei:progressbar:0.10.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateGrammarSource"))
}

tasks.named("compileTestKotlin") {
    dependsOn(tasks.named("generateGrammarSource"))
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}