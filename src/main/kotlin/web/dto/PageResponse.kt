package org.example.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    val items: List<T>,
    val limit: Int,
    val offset: Int,
    val total: Int
)