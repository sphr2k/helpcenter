package io.bootify.helpcenter.help

import com.google.cloud.spring.storage.GoogleStorageResource
import com.google.cloud.storage.Storage
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Component
@ConditionalOnProperty(prefix = "helpfiles", name = ["cloud-provider"], havingValue = "gcp")
class GcpObjectStorage(
    private val storage: Storage,
    private val props: HelpResourceProperties,
) : ObjectStorage {
    override fun list(prefix: String): List<String> =
        try {
            storage
                .list(
                    props.bucketName,
                    Storage.BlobListOption.prefix(prefix),
                    Storage.BlobListOption.currentDirectory(),
                ).iterateAll()
                .map { it.name }
                .filter { it.isNotBlank() && !it.endsWith("/") }
                .map { it.removePrefix(prefix) }
        } catch (ex: com.google.cloud.storage.StorageException) {
            val code = if (ex.code == 403) "GCS_FORBIDDEN" else "GCS_ERROR"
            throw StorageAccessException(code, "GCS access failed for bucket ${props.bucketName}: ${ex.message}", ex)
        } catch (ex: Exception) {
            throw StorageAccessException("GCS_ERROR", "GCS operation failed for bucket ${props.bucketName}", ex)
        }

    override fun get(key: String): Resource =
        try {
            // Lazy; errors surface on read
            GoogleStorageResource(storage, "gs://${props.bucketName}/$key")
        } catch (ex: com.google.cloud.storage.StorageException) {
            val code =
                when (ex.code) {
                    404 -> "GCS_NOT_FOUND"
                    403 -> "GCS_FORBIDDEN"
                    else -> "GCS_ERROR"
                }
            throw StorageAccessException(code, "GCS object access failed: $key", ex)
        } catch (ex: Exception) {
            throw StorageAccessException("GCS_ERROR", "GCS operation failed for key $key", ex)
        }
}
