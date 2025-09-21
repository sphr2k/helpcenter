package io.bootify.helpcenter.help

interface ObjectUrlSigner {
    fun signGetUrl(key: String, contentType: String? = null): String
}
