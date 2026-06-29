dependencies {
    api("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3")
    api("org.apache.fory:fory-core:1.1.0")
    api("com.github.f4b6a3:uuid-creator:6.1.1")
    api("org.springframework.boot:spring-boot-starter")
    api("tools.jackson.core:jackson-core")
    api("tools.jackson.core:jackson-databind")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
