plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "dev.evagomez"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
    //cache spring
    implementation("org.springframework.boot:spring-boot-starter-cache:3.1.0")
    //validation
    implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
    implementation("com.h2database:h2:2.1.214")
    //websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.0.4")
    //recibe en formato xml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")
    //MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.0.7")
    // Thyemeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.0.4")
    // Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")
    // Dependencia para test
    testImplementation("org.springframework.security:spring-security-test:6.0.2")
    //auth0
    implementation("com.auth0:java-jwt:4.4.0")
    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
