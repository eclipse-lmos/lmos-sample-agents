// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.sample.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class HtmlGenerator(@Value("\${report.path}") val reportPath: String) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // Function to generate Html
    fun generateHtml(content: String, outputFile: String): String {
        try {
            val filePath = "$reportPath/$outputFile";
            // Create the directory if it doesn't exist
            val directory = File(reportPath)
            if (!directory.exists()) {
                directory.mkdirs()  // Creates the directory and any necessary parent directories
            }
            val file = File(filePath)
            val regex = "(?s)(<!DOCTYPE[^>]*>.*?</html>)".toRegex()
            val parseHtmlContent = regex.find(content)?.value ?: content
            file.writeText(parseHtmlContent)
        } catch (e: Exception) {
            return "The report was not generated due to some issues"
        }
        return "The Report has been generated successfully at $outputFile"
    }
}