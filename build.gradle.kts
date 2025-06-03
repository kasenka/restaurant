plugins {
    application
    id("java")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("io.freefair.lombok") version "8.6"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {mainClass.set("org.exmaple.Application")}

repositories {
    mavenCentral()
}

dependencies {
    // 1. Основные Spring Boot стартеры
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-devtools")

    // 2. База данных
    implementation("org.postgresql:postgresql:42.6.0")

    // 3. Безопасность
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // 4. Jakarta/Java EE спецификации
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // 5. Lombok
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.test {
    useJUnitPlatform()
}