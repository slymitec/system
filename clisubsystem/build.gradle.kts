plugins {
    java
    id("org.springframework.boot") version ("3.5.4")
    id("io.spring.dependency-management") version ("1.1.7")
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
}

allprojects {
    group = projectNameSpace
    version = projectVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    tasks.bootJar {
        enabled = false
    }

    tasks.jar {
        enabled = true
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

var projectName = "SLY CliSubSystem"
var projectVersion = "1.0.0.0"
var projectNameSpace = "indi.sly.system"
var javaLanguageVersion = 21
var textEncoding = "UTF-8";

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaLanguageVersion)
    }
}

tasks.compileJava {
    options.encoding = textEncoding
    options.release.set(javaLanguageVersion)
}

tasks.compileTestJava {
    options.encoding = textEncoding
    options.release.set(javaLanguageVersion)
}

tasks.jar {
    enabled = true
    manifest {
        attributes(
            "Implementation-Title" to projectName, "Implementation-Version" to projectVersion
        )
    }
}

tasks.bootJar {
    enabled = true
    mainClass.set("indi.sly.system.boot.SystemBoot")
    manifest {
        attributes(
            "Implementation-Title" to projectName, "Implementation-Version" to projectVersion
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}