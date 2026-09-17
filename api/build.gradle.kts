plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.slf4j.api);
    api(libs.fastutil)
    api(libs.netty.buffer)

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
