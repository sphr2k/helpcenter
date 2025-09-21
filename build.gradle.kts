import org.springframework.boot.gradle.tasks.run.BootRun
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
}

group = "io.bootify"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(21))
    }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:3.4.0")
    implementation("com.google.cloud:spring-cloud-gcp-starter-storage:7.3.1")
    implementation("software.amazon.awssdk:sts:2.34.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<BootRun>("bootRun") {
    environment["SPRING_PROFILES_ACTIVE"] = environment["SPRING_PROFILES_ACTIVE"] ?: "local"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
