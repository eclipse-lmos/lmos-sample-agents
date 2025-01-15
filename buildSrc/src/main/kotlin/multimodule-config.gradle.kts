// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

import com.vanniktech.maven.publish.SonatypeHost
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.lang.System.getenv
import java.net.URI

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.graalvm.buildtools.native")
    id("com.citi.helm")
    id("com.citi.helm-publish")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xcontext-receivers")
    }
}

group = "org.eclipse.lmos"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        name = "LMOS Sample Agents"
        description =
            """Sample agents for deploying to an LMOS instance
            """.trimMargin()
        url = "https://github.com/eclipse-lmos/lmos-sample-agents"
        licenses {
            license {
                name = "Apache-2.0"
                distribution = "repo"
                url = "https://github.com/eclipse-lmos/lmos-sample-agents/blob/main/LICENSES/Apache-2.0.txt"
            }
        }
        developers {
            developer {
                id = "telekom"
                name = "Telekom Open Source"
                email = "opensource@telekom.de"
            }
        }
        scm {
            url = "https://github.com/eclipse-lmos/lmos-sample-agents.git"
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/eclipse-lmos/lmos-sample-agents")
            credentials {
                username = findProperty("GITHUB_USER")?.toString() ?: getenv("GITHUB_USER")
                password = findProperty("GITHUB_TOKEN")?.toString() ?: getenv("GITHUB_TOKEN")
            }
        }
    }
}

helm {
    charts {
        create("main") {
            chartName.set("${project.name}-chart")
            chartVersion.set("${project.version}")
            sourceDir.set(file("src/main/helm"))
        }
    }
}

dependencies {
    val arcVersion = "0.1.0-SNAPSHOT"
    val kotlinXVersion = "1.8.0"
    val kotlinSerialization = "1.7.1"
    val springBootVersion = "3.3.3"

    // Platform/BOM Dependencies
    implementation(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))

    // Arc Dependencies
    implementation("org.eclipse.lmos:arc-scripting:$arcVersion")
    implementation("org.eclipse.lmos:arc-azure-client:$arcVersion")
    implementation("com.azure:azure-identity:1.13.1")
    implementation("org.eclipse.lmos:arc-spring-boot-starter:$arcVersion")
    implementation("org.eclipse.lmos:arc-ollama-client:$arcVersion")
    implementation("org.eclipse.lmos:arc-reader-html:$arcVersion")
    implementation("org.eclipse.lmos:arc-graphql-spring-boot-starter:$arcVersion")

    // Kotlin Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerialization")

    // Spring Boot Dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Test
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:mongodb:1.19.7")

    //Ktor
    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.1")

    //pdf
    implementation("com.itextpdf:itext7-core:7.1.15")

    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.30.0")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("helmPush") {
    description = "Push Helm chart to OCI registry"
    group = "helm"
    dependsOn(tasks.named("helmPackageMainChart"))

    doLast {
        val registryUrl = getProperty("REGISTRY_URL")
        val registryUsername = getProperty("REGISTRY_USERNAME")
        val registryPassword = getProperty("REGISTRY_PASSWORD")
        val registryNamespace = getProperty("REGISTRY_NAMESPACE")

        helm.execHelm("registry", "login") {
            option("-u", registryUsername)
            option("-p", registryPassword)
            args(registryUrl)
        }

        helm.execHelm("push") {
            args(tasks.named("helmPackageMainChart").get().outputs.files.singleFile.toString())
            args("oci://${registryUrl}/${registryNamespace}")
        }

        helm.execHelm("registry", "logout") {
            args(registryUrl)
        }
    }
}

fun getProperty(propertyName: String) = System.getenv(propertyName) ?: project.findProperty(propertyName) as String
