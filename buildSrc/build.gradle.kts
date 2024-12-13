// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
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
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.0.20")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.20")
    implementation("org.graalvm.buildtools:native-gradle-plugin:0.10.2")
    implementation("com.citi.gradle-plugins.helm:helm-plugin:2.2.0")
    implementation("com.citi.gradle-plugins.helm:helm-publish-plugin:2.2.0")
}