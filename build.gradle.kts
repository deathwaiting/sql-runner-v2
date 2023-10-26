plugins {
    java
    id("io.quarkus")
    id("com.appland.appmap") version "1.2.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkus:quarkus-reactive-mysql-client")
    implementation("io.quarkus:quarkus-reactive-oracle-client")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-reactive-mssql-client")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkiverse.jdbi:quarkus-jdbi:1.2.1")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-reactive-db2-client")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-cache")
    implementation ("io.quarkus:quarkus-elytron-security")
    implementation ("io.quarkus:quarkus-elytron-security-properties-file")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("net.javacrumbs.json-unit:json-unit:2.36.1")
}

group = "org.galal"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
