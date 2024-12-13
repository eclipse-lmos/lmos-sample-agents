// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import org.eclipse.lmos.arc.sample.services.HtmlGenerator

function(
    name = "generateReport",
    description = "Generate report of product catalog",
    params = types(
        string(
            name = "html_content",
            description = "A html data having product catalog information"
        )
    ),
) { (query) ->
    val htmlGenerate = get<HtmlGenerator>()
    val contextResult = query?.let { htmlGenerate.generateHtml(it, "Product_Recommendation_Report.html") }
    """
        $contextResult
    """.trimIndent()
}

