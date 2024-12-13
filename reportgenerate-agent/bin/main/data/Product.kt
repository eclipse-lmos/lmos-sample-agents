// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.lmos.arc.sample.data

data class Product(
    val name: String,
    val link:String?,
    val thumbnail: String,
    val price: String,
    val review: String
)

data class Category(
    val category: String,
    val products: List<Product>
)