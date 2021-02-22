plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":sample-processor"))
    ksp(project(":sample-processor"))
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}
