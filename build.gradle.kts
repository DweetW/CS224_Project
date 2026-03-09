plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "edu.bsu.cs"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.slf4j:slf4j-nop:2.0.11")
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("net.minidev:json-smart:2.5.0")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "22"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    // GUI entry point
    mainClass.set("edu.bsu.cs.GuiMain")
}


