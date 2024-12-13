// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.sample.services

import org.eclipse.lmos.arc.sample.data.Category
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TextAlignment
import com.nimbusds.jose.shaded.gson.GsonBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File


@Component
class PdfGenerator(@Value("\${report.path}") val reportPath: String) {
    val gson = GsonBuilder().setLenient().create()
    private val logger = LoggerFactory.getLogger(javaClass)

    // Function to generate PDF
    fun generatePdf(content: String, outputFile: String): String {
        val filePath = "$reportPath/$outputFile";
        // Create the directory if it doesn't exist
        val directory = File(reportPath)
        if (!directory.exists()) {
            directory.mkdirs()  // Creates the directory and any necessary parent directories
        }
        // Create a PDF writer instance
        val pdfWriter = PdfWriter(filePath)
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)
        var generateReport: Boolean = true
        try {
            // Title
            val title = Paragraph("Product Recommendation")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20f)
                .setBold()
            document.add(title)

            var correctedJson = content.trimIndent()
            //Handle such scenario
            if (correctedJson.endsWith('}')) {
                correctedJson = correctedJson.substring(0, correctedJson.length - 1);
            }
            logger.info("Json_Data: $correctedJson")

            // Fix issues such as unescaped quotes or improper characters
            correctedJson = correctedJson.replace("[^\\x00-\\x7F]".toRegex(), "")
            correctedJson = correctedJson.replace("“", "\"").replace("”", "\"")
            correctedJson = correctedJson.replace("\\\"", "\"")
            correctedJson = correctedJson.replace("\\${'$'}", "$")
            correctedJson =
                correctedJson.replace("\"[ ]*([,}])".toRegex(), "\"$1")  // Clean up misplaced spaces after quotes

            logger.info("After Sanitization: $correctedJson")
            // Try to parse the JSON into a list of Category objects
            val categories = gson.fromJson(correctedJson, Array<Category>::class.java).toList()

            var serialNumber = 1

            // Iterate through each category in the JSON array
            for (category in categories) {
                val categoryName = category.category
                val products = category.products

                // Add category section header
                val categoryHeader = Paragraph(categoryName)
                    .setBold()
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20f)
                document.add(categoryHeader)

                // Create a table with 5 columns: S.No., Name, Thumbnail, Price, Review
                val table = Table(floatArrayOf(1f, 3f, 3f, 2f, 2f))
                table.addHeaderCell(
                    Cell().add(Paragraph("S.No.").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                table.addHeaderCell(
                    Cell().add(Paragraph("Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                table.addHeaderCell(
                    Cell().add(Paragraph("Thumbnail Image").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                table.addHeaderCell(
                    Cell().add(Paragraph("Price").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                table.addHeaderCell(
                    Cell().add(Paragraph("Review").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )

                // Populate the table with product data
                for (product in products) {
                    try {
                        val name = product.name
                        val link = product.link
                        val thumbnailUrl = product.thumbnail
                        val price = product.price
                        val review = product.review

                        val productLinkName = "  Link"
                        // Create a Link object with the display text and URL
                        val paragraphWithNameLink: Paragraph =
                            Paragraph().add(name).add(
                                Link(productLinkName, PdfAction.createURI(link))
                                    .setFontColor(ColorConstants.BLUE)
                                    .setUnderline()
                            ) // Optional: to underline the

                        table.addCell(Cell().add(Paragraph(serialNumber.toString())))
                        table.addCell(Cell().add(paragraphWithNameLink).setTextAlignment(TextAlignment.LEFT))

                        // Add thumbnail image
                        if (thumbnailUrl.contains("http://") || thumbnailUrl.contains("https://")) {
                            val imageData = ImageDataFactory.create(thumbnailUrl)
                            val image = Image(imageData).setWidth(50f).setHeight(50f)
                            table.addCell(Cell().add(image))
                        } else {
                            table.addCell(Cell().add(Paragraph(thumbnailUrl)))
                        }
                        //process Price
                        table.addCell(Cell().add(Paragraph(checkForDigits(price))))
                        table.addCell(Cell().add(Paragraph(checkForDigits(review))))

                        serialNumber++
                    } catch (e: Exception) {
                        generateReport = false
                        logger.error("Exception While parse Product information", e)
                    }
                }

                // Add table to document
                document.add(table)
            }
        } catch (e: Exception) {
            generateReport = false
            logger.error("Exception occur while process JSON_DATA", e)
            e.printStackTrace()
        } finally {
            // Close the document
            document.close()
        }
        if (!generateReport) {
            return "Report Not Generated Due to Some Issues"
        }
        return "PDF created successfully at $outputFile"
    }

    fun checkForDigits(text: String): String {
        return if (text.any { it.isDigit() }) text else "-"
    }
}