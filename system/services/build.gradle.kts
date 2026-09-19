dependencies {
    implementation(project(":common"))
    implementation(project(":kernel"))

    api("io.dapr.spring:dapr-spring-boot-starter:1.18.1")
    api("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
