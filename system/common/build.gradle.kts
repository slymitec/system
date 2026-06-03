dependencies {
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.fory:fory-core:0.17.0")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("tools.jackson.core:jackson-core")
    implementation("tools.jackson.core:jackson-databind")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
