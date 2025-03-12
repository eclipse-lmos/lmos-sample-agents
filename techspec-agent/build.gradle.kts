// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("multimodule-config")
    id("net.researchgate.release") version "3.1.0"
}

release {
    newVersionCommitMessage = "New Snapshot-Version:"
    preTagCommitMessage = "Release:"
}