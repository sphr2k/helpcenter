package io.bootify.helpcenter.help

import org.springframework.core.io.Resource

interface ObjectStorage {
    fun list(prefix: String): List<String>

    fun get(key: String): Resource
}
