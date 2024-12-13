// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.lmos.arc.sample.data

import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse(
    val items: List<Items>
)

@Serializable
data class Items(
    val title: String,
    val htmlTitle: String,
    val displayLink: String,
    val snippet: String,
    val formattedUrl: String,
    val link: String,
    val pagemap: PageMap
)

@Serializable
data class PageMap(
    val cse_thumbnail: List<Thumbnail>? = null,
    val cse_image: List<Image>? = null,
    val product: List<ProductInfo>? = null,
    val aggregaterating: List<Rating>? = null,
    val metatags: List<Map<String, String>>? = null,
    val offer: List<Offer>? = null,
)

@Serializable
data class Thumbnail(
    val src: String? = null,
)

@Serializable
data class Image(
    val src: String? = null,
)

@Serializable
data class ProductInfo(
    val name: String? = null,
)

@Serializable
data class Rating(
    val ratingvalue: String? = null,
    val reviewcount: String? = null,
)

@Serializable
data class Offer(
    val price: String? = null,
    val pricecurrency: String? = null,
)


@Serializable
data class Product(
    val name: String,
    val siteName: String,
    val link: String,
    val snippet: String,
    val thumbnail: String?,
    val imageUrl: String?,
    val rating: List<Rating>?,
    val metadata: List<Map<String, String>>?,
    val offer: List<Offer>?
)