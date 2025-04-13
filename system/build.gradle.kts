plugins {
    java
    id("org.springframework.boot") version ("3.4.4")
    id("io.spring.dependency-management") version ("1.1.7")
    kotlin("jvm") version ("1.9.25")
    kotlin("plugin.spring") version ("1.9.25")
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
    implementation(project(":services"))

    implementation("com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1.MR")
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
    options.release.set(21)
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
    options.release.set(21)
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs += "-Xjsr305=strict"
//        jvmTarget = "21"
//    }
//}

tasks.jar {
    enabled = true
    manifest {
        attributes(
            "Implementation-Title" to "SLY System",
            "Implementation-Version" to "1.0.0.0"
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}