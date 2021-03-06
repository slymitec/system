plugins {
    java
    id("org.springframework.boot") version ("2.5.2")
    id("io.spring.dependency-management") version ("1.0.11.RELEASE")
}

allprojects {
    group = "indi.sly.system"
    version = "1.0"

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
    implementation(project(":kernel"))

    implementation("com.microsoft.sqlserver:mssql-jdbc:9.2.1.jre11")
    implementation("javax.inject:javax.inject:1")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.bootJar {
    enabled = true
    mainClass.set("indi.sly.system.boot.SystemBoot")
    manifest {
        attributes(
            "Implementation-Title" to "SLY System",
            "Implementation-Version" to "1.0.0.0"
        )
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(11)
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
    options.release.set(16)
}

tasks.jar {
    enabled = true
    manifest {
        attributes(
            "Implementation-Title" to "SLY System",
            "Implementation-Version" to "1.0.0.0"
        )
    }
}

tasks.test {
    useJUnitPlatform()
}
