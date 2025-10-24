val junitVersion = "6.0.0"
val jteVersion = "3.2.1"
val javalinVersion = "6.7.0"
val sl4jVersion = "2.0.17"
val hikariVersion = "7.0.2"
val h2Version = "2.4.240"
val postgresVersion = "42.7.8"
val lombokVersion = "1.18.42"

plugins {
    id("application")
    id("checkstyle")
    id("jacoco")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.0.0.6105"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("hexlet.code.App")
}

checkstyle {
    toolVersion = "11.1.0"
    configFile = file("$rootDir/config/checkstyle.xml")
}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

sonar {
    properties {
        property("sonar.projectKey", "DaniilKornilov_java-project-72")
        property("sonar.organization", "daniilkornilov")
    }
}

dependencies {
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-rendering:$javalinVersion")

    implementation("org.slf4j:slf4j-simple:$sl4jVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.postgresql:postgresql:$postgresVersion")

    implementation("gg.jte:jte:$jteVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
