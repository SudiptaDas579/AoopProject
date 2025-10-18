plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("org.example.aoopproject")
    mainClass.set("org.example.aoopproject.HelloApplication")
}

javafx {
    version = "24.0.1"
    configuration = "implementation"
    modules = listOf(
        "javafx.controls", "javafx.fxml", "javafx.web",
        "javafx.swing", "javafx.media"
    )
}

dependencies {
    implementation ("com.sun.mail:jakarta.mail:2.0.1")
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") { exclude(group = "org.openjfx") }
    implementation("net.synedra:validatorfx:0.6.1") { exclude(group = "org.openjfx") }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.9") { exclude(group = "org.openjfx") }

    implementation("com.github.almasb:fxgl:17.3") {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("org.json:json:20240303")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(
        "--add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.media,javafx.swing",
        "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
        "--add-opens=javafx.web/com.sun.webkit=ALL-UNNAMED",
        "--add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED",
        "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
        "-Dfile.encoding=UTF-8",
        "-Dprism.order=sw"
    )
}

tasks.register<Exec>("runChatServer") {
    group = "application"
    description = "Run the chat server in a background process"
    commandLine(
        "java",
        "--add-modules=javafx.controls,javafx.fxml,javafx.web",
        "-Dfile.encoding=UTF-8",
        "-Dprism.order=sw",
        "-cp", sourceSets["main"].runtimeClasspath.asPath,
        "org.example.aoopproject.server.ChatServer"
    )
}



jlink {
    imageZip.set(layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
