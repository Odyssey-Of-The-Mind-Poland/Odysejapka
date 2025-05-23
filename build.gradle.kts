import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  id("org.springframework.boot") version "2.3.4.RELEASE"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.3.72"
  kotlin("plugin.jpa") version "1.3.72"
}

group = "odyseja"
version = ""
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-jdbc:2.5.5")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("commons-io:commons-io:2.11.0")
  implementation("org.springdoc:springdoc-openapi-ui:1.6.3")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
  implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
  implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
  implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
  implementation("org.postgresql:postgresql:42.2.23")
  implementation("com.opencsv:opencsv:5.10")
  implementation("org.thymeleaf:thymeleaf:3.1.3.RELEASE")
  implementation("org.thymeleaf:thymeleaf-spring5:3.1.3.RELEASE")
  implementation("com.openhtmltopdf:openhtmltopdf-core:1.0.10")
  implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")


  runtimeOnly("com.h2database:h2:1.4.200")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
  useJUnitPlatform()
}


tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}
