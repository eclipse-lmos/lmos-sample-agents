// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.lmos.arc.sample.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Paths

@RestController
class ReportGenerateController(@Value("\${report.path}") val pdfDirectory: String) {

    @GetMapping("/download/{fileName}")
    fun downloadPDF(@PathVariable fileName: String): ResponseEntity<Resource> {
        // Create the full file path
        val filePath = Paths.get(pdfDirectory, fileName)

        // Load the file as a resource
        val resource: Resource = FileSystemResource(filePath)

        // Check if the file exists
        if (!resource.exists()) {
            return ResponseEntity.notFound().build()
        }

        // Set content-disposition header for download
        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            contentType = MediaType.APPLICATION_PDF
        }

        // Return the file as a response entity
        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(resource.file.length())
            .body(resource)
    }
}