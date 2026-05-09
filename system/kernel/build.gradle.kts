dependencies {
    implementation(project(":common"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("cn.hutool:hutool-all:5.8.44")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.security:spring-security-acl")
    implementation("com.redis.om:redis-om-spring:2.0.4")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
