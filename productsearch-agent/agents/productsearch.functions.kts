// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0
import org.eclipse.lmos.arc.sample.services.ProductSearch

function(
    name = "productsearch",
    description = "Search products based on the specifications",
    params = types(
        string(
            name = "query",
            description = "A query with technical descriptions of the products or user required specifications"
        )
    ),
) { (query) ->
    val productSearch = get<ProductSearch>()
    val contextResult = query?.let { productSearch.searchProduct(it, "") }
    """
        $contextResult
    """.trimIndent()
}

