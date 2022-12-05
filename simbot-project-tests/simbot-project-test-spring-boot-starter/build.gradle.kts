plugins {
    java
    kotlin("jvm") // version "1.7.21"
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":simbot-boots:simboot-core-spring-boot-starter"))
    implementation("love.forte.simbot.component:simbot-component-mirai-core:3.0.0.0-beta.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
