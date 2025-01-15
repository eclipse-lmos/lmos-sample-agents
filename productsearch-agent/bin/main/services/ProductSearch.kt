// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.lmos.arc.sample.services

import org.eclipse.lmos.arc.sample.data.ApiResponse
import org.eclipse.lmos.arc.sample.data.Product
import com.nimbusds.jose.shaded.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class ProductSearch(
    @Value("\${google.search.engine.key}") private val searchEngineKey: String,
    @Value("\${google.cloud.api.key}") private val cloudApiKey: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val gson = Gson()

    //This function will extract products based on query using custom google search engine
    suspend fun searchProduct(query: String, siteSearch: String): List<Product> {
        // Initialize the Ktor client
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }) // Configure JSON deserialization
            }
        }
        return runBlocking {
            try {
                // Make the GET request and parse JSON into ProductResponse
                val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                logger.info("Query to search: $query")
                val productList: List<Product>
                logger.info("Your Keys are: $searchEngineKey and $cloudApiKey") //To-DO this needs to be removed after testing
                if (cloudApiKey.isEmpty() || searchEngineKey.isEmpty()) {
                    throw Exception("Environment Variables are not set check Cloud and Search Engine Keys!")
                } else {
                    val url =
                        "https://www.googleapis.com/customsearch/v1?key=$cloudApiKey&cx=$searchEngineKey&q=$encodedQuery&googlehost=google.com&lr=lang_en&alt=json"
                    logger.info("API Request URL: $url")
                    val response: ApiResponse = client.get(url).body()
                    //meta-data is huge which might cause token issue with llm so extract only price and currency
                    val regex = Regex("(price|currency)")
                    productList = response.items.map { item ->
                        Product(
                            name = item.title,
                            siteName = item.displayLink,
                            link = item.link,
                            snippet = item.snippet,
                            thumbnail = item.pagemap.cse_thumbnail?.get(0)?.src,
                            imageUrl = item.pagemap.cse_image?.get(0)?.src,
                            rating = item.pagemap.aggregaterating,
                            metadata = extractRequiredMetaData(item.pagemap.metatags, regex),
                            offer = item.pagemap.offer
                        )
                    }
                }
                //print response in json
                logger.debug(gson.toJson(productList))
                productList
            } catch (e: Exception) {
                logger.error("Error fetching product data: ${e.message} \n stacktrace : ${e.stackTrace}")
                emptyList()
            } finally {
                client.close()
            }
        }
    }

    //this function extract only required or mandatory information from meta-tags
    private fun extractRequiredMetaData(
        metadata: List<Map<String, String>>?, regex: Regex
    ): List<Map<String, String>> {
        val extractedMetaTags = mutableListOf<Map<String, String>>()
        if (metadata != null) {
            for (map in metadata) {
                for ((key, value) in map) {
                    if (regex.containsMatchIn(key)) {
                        // If regex matches the key, add it to the new list
                        extractedMetaTags.add(mapOf(key to value))
                    }
                }
            }
        }
        return extractedMetaTags
    }
}