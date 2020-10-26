plugins {
    java
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

dependencies {
    implementation("javax.inject:javax.inject:1")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = false
}

