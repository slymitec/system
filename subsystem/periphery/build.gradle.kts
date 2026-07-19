dependencies {
    implementation(project(":common"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-starter-restclient")
    api("org.redisson:redisson-spring-boot-starter:4.6.1")
    api("org.java-websocket:Java-WebSocket:1.6.0")
    api("tools.jackson.core:jackson-core")
    api("tools.jackson.core:jackson-databind")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
