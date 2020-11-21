plugins {
    java
    war
    id("org.springframework.boot") version ("2.4.0")
}

apply(plugin = "io.spring.dependency-management")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

    implementation("com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11")
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
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = true
}

tasks.test {
    useJUnitPlatform()
}
