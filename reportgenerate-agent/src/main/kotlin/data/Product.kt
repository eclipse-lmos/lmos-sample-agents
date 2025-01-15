// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
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