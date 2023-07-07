import com.google.cloud.tools.jib.api.buildplan.ImageFormat.OCI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.medicaldataprovider"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("mysql:mysql-connector-java:8.0.33")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "amazoncorretto:17.0.7"
    }
    container {
        mainClass = "com.medicaldataprovider.MedicalDataProviderApplicationKt"
        ports = listOf("8080")
        format = OCI
    }
}
