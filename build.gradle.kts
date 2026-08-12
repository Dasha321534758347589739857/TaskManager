plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    // application - УБИРАЕМ ЕГО СОВСЕМ, так как он не нужен
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "3.1.1"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:1.13.13")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    implementation("io.ktor:ktor-server-status-pages:3.1.1")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.zaxxer:HikariCP:5.1.0")

}


kotlin {
    jvmToolchain(23)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.withType<JavaExec> {
    systemProperty("file.encoding", "UTF-8")
}



tasks.test {
    useJUnitPlatform()
}




tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run HTTP server"
    mainClass.set("org.example.ApplicationKt")
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
    systemProperty("file.encoding", "UTF-8")
}

// Задача для запуска CLI
tasks.register<JavaExec>("runCli") {
    group = "application"
    description = "Run CLI application"
    mainClass.set("org.example.MainKt")  // или CliMainKt - как у вас называется
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
}