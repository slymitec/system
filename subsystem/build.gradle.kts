plugins {
    java
    id("org.springframework.boot") version ("4.0.5")
    id("io.spring.dependency-management") version ("1.1.7")
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
    implementation(project(":common"))
    implementation(project(":periphery"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.redisson:redisson-spring-boot-starter:4.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

var projectName = "SLY SubSystem"
var projectVersion = "1.0.0.0"
var projectNameSpace = "indi.sly.subsystem"
var javaLanguageVersion = 25
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
    mainClass.set("indi.sly.subsystem.boot.CLISubSystemBoot")
    manifest {
        attributes(
            "Implementation-Title" to projectName, "Implementation-Version" to projectVersion
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}