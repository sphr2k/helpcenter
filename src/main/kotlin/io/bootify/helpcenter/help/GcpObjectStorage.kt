package io.bootify.helpcenter.help

import com.google.cloud.storage.Blob
import com.google.cloud.storage.Storage
import com.google.cloud.storage.Storage.BlobListOption
import com.google.cloud.spring.storage.GoogleStorageResource
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets

@Component
@ConditionalOnProperty(prefix = "helpfiles", name = ["cloud-provider"], havingValue = "gcp")
class GcpObjectStorage(
    private val storage: Storage,
    private val props: HelpResourceProperties
) : ObjectStorage {

    override fun list(prefix: String): List<String> {
        val page = storage.list(
            props.bucketName,
            BlobListOption.prefix(prefix),
            BlobListOption.currentDirectory()
        )
        return page.iterateAll()
            .map(Blob::getName)
            .filter { it.isNotBlank() && !it.endsWith("/") }
            .map { it.removePrefix(prefix) }
    }

    override fun get(key: String): Resource {
        val encoded = UriUtils.encodePath(key, StandardCharsets.UTF_8)
        return GoogleStorageResource(storage, "gs://${props.bucketName}/$encoded")
    }
}
