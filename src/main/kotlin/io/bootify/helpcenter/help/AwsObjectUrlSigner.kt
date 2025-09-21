package io.bootify.helpcenter.help

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Component
@ConditionalOnProperty(
        prefix = "helpfiles",
        name = ["cloud-provider"],
        havingValue = "aws",
        matchIfMissing = true
)
class AwsObjectUrlSigner(
        private val presigner: software.amazon.awssdk.services.s3.presigner.S3Presigner,
        private val props: HelpResourceProperties
) : ObjectUrlSigner {
        override fun signGetUrl(key: String, contentType: String?): String {
                val get =
                        software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                                .bucket(props.bucketName)
                                .key(key)
                                .build()
                val req =
                        software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
                                .builder()
                                .signatureDuration(props.signedUrlTtl)
                                .getObjectRequest(get)
                                .build()
                return presigner.presignGetObject(req).url().toString()
        }
}
