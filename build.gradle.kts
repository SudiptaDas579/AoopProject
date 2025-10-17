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
    // ✅ Add Sonatype repo for ControlsFX newer versions
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
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
    val javafxModules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.web",
        "javafx.swing",
        "javafx.media"
    )
    val modulesArg = javafxModules.joinToString(",")

    applicationDefaultJvmArgs = listOf(
        "--add-modules", modulesArg,
        "--add-opens", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
        "--add-opens", "javafx.web/com.sun.webkit=ALL-UNNAMED",
        "--add-opens", "javafx.web/com.sun.javafx.scene.web=ALL-UNNAMED",
        "--add-opens", "javafx.controls/javafx.scene.control=ALL-UNNAMED",
        "--add-opens", "javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
        "-Dfile.encoding=UTF-8"
    )
}

javafx {
    version = "24.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing", "javafx.media")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")

    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("net.synedra:validatorfx:0.6.1") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.9") {
        exclude(group = "org.openjfx")
    }
    implementation("com.github.almasb:fxgl:17.3") {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
    implementation("org.json:json:20240303")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
