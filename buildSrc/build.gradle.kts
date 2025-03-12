// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlin = "2.1.10"
    val helm = "2.2.0"
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.4.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlin")
    implementation("org.graalvm.buildtools:native-gradle-plugin:0.10.2")
    implementation("com.citi.gradle-plugins.helm:helm-plugin:$helm")
    implementation("com.citi.gradle-plugins.helm:helm-publish-plugin:$helm")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.31.0")
}