package io.bootify.helpcenter.help

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "helpfiles", name = ["cloud-provider"], havingValue = "gcp")
class GcpObjectUrlSigner(
    private val storage: com.google.cloud.storage.Storage,
    private val props: HelpResourceProperties,
) : ObjectUrlSigner {
    override fun signGetUrl(
        key: String,
        contentType: String?,
    ): String {
        val blob =
            com.google.cloud.storage.BlobInfo
                .newBuilder(props.bucketName, key)
                .setContentType(contentType ?: "application/octet-stream")
                .build()
        val url =
            storage.signUrl(
                blob,
                props.signedUrlTtl.seconds,
                java.util.concurrent.TimeUnit.SECONDS,
                com.google.cloud.storage.Storage.SignUrlOption
                    .httpMethod(com.google.cloud.storage.HttpMethod.GET),
                com.google.cloud.storage.Storage.SignUrlOption
                    .withV4Signature(),
            )
        return url.toString()
    }
}
