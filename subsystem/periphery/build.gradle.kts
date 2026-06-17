dependencies {
    implementation(project(":common"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.redisson:redisson-spring-boot-starter:4.6.0")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
    implementation("tools.jackson.core:jackson-core")
    implementation("tools.jackson.core:jackson-databind")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
