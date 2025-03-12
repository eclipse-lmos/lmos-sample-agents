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
    //id("net.researchgate.release")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xcontext-receivers")
    }
}

fun getProperty(propertyName: String) = System.getenv(propertyName) ?: project.findProperty(propertyName) as String
group = "org.eclipse.lmos"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
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

//    release {
//        newVersionCommitMessage = "New Snapshot-Version:"
//        preTagCommitMessage = "Release:"
//    }

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

tasks.named<BootBuildImage>("bootBuildImage") {
    group = "publishing"
    if ((System.getenv("REGISTRY_URL") ?: project.findProperty("REGISTRY_URL")) != null) {
        val registryUrl = getProperty("REGISTRY_URL")
        val registryUsername = getProperty("REGISTRY_USERNAME")
        val registryPassword = getProperty("REGISTRY_PASSWORD")
        val registryNamespace = getProperty("REGISTRY_NAMESPACE")

        imageName.set("$registryUrl/$registryNamespace/${project.name}:${project.version}")
        publish = true
        docker {
            publishRegistry {
                url.set(registryUrl)
                username.set(registryUsername)
                password.set(registryPassword)
            }
        }
    } else {
        imageName.set("${project.name}:${project.version}")
        publish = false
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
    val arcVersion = "0.121.0"
    val kotlinXVersion = "1.10.1"
    val kotlinSerialization = "1.8.0"
    val springBootVersion = "3.4.3"

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
    testImplementation("org.testcontainers:mongodb:1.20.6")

    //Ktor
    val ktorVersion = "3.1.1"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.0")

    //pdf
    implementation("com.itextpdf:itext-core:9.1.0")

    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.31.0")
    //implementation("net.researchgate.release:3.1.0")
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
    group = "publishing"
    dependsOn(tasks.named("helmPackageMainChart"))

    doLast {
        if ((System.getenv("REGISTRY_URL") ?: project.findProperty("REGISTRY_URL")) != null) {
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
                args(
                    tasks
                        .named("helmPackageMainChart")
                        .get()
                        .outputs.files.singleFile
                        .toString(),
                )
                args("oci://$registryUrl/$registryNamespace")
            }

            helm.execHelm("registry", "logout") {
                args(registryUrl)
            }
        }
    }
}

tasks.named("publish") {
    dependsOn(tasks.named<BootBuildImage>("bootBuildImage"))
    dependsOn(tasks.named("helmPush"))
}