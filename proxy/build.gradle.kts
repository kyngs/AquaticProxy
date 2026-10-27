plugins {
    id("java")
}

group = "xyz.kyngs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":api"))

    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.boosted.yaml)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
