package io.bootify.helpcenter.help

class StorageAccessException(
    val code: String,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
