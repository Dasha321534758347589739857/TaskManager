package org.example.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse (
    val error: String,
    val message: String,
    val status: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val details: Map<String, String>? = null
)
{
    companion object {
        fun badRequest(
            message: String,
            path: String? = null,
            details: Map<String, String>? = null
        ) = ErrorResponse(
            error = "Ошибка запроса",
            message = message,
            status = 400,
            path = path,
            details = details
        )

        fun notFound(
            message: String,
            path: String? = null
        ) = ErrorResponse(
            error = "Не найдено",
            message = message,
            status = 404,
            path = path
        )

        fun internalServerError(
            message: String,
            path: String? = null
        ) = ErrorResponse(
            error = "Внутренняя ошибка сервера",
            message = message,
            status = 500,
            path = path
        )

        fun conflict(
            message: String,
            path: String? = null
        ) = ErrorResponse(
            error = "Текущее состояние процесса не соответствует выполняемому действию",
            message = message,
            status = 409,
            path = path
        )
    }
}