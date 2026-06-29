plugins {
    java
    id("org.springframework.boot") version ("4.1.0")
    id("io.spring.dependency-management") version ("1.1.7")
}

val projectName = "SLY System"
val projectVersion = "1.0.0.0"
val projectNameSpace = "indi.sly.system"
val javaLanguageVersion = 25
val textEncoding = "UTF-8"

allprojects {
    group = projectNameSpace
    version = projectVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    pluginManager.apply("java-library")
    pluginManager.apply("org.springframework.boot")
    pluginManager.apply("io.spring.dependency-management")

    tasks.bootJar {
        enabled = false
    }

    tasks.jar {
        enabled = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kernel"))
    implementation(project(":services"))

    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:13.4.0.jre11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

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