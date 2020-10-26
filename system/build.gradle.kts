plugins {
    java
    id("io.spring.dependency-management") version ("1.0.10.RELEASE")
    id("org.springframework.boot") version ("2.3.4.RELEASE")
}

allprojects {
    group = "indi.sly.system"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kernel"))

    implementation("com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11")
    implementation("javax.inject:javax.inject:1")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.compileJava {
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = true
}