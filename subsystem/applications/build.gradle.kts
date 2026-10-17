dependencies {
    implementation(project(":common"))
    implementation(project(":periphery"))

    implementation("cn.dev33:sa-token-spring-boot4-starter:1.46.0")
    implementation("cn.dev33:sa-token-redisson-spring-boot-starter:1.46.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
