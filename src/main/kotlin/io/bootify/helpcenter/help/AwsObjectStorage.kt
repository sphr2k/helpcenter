package io.bootify.helpcenter.help

import io.awspring.cloud.s3.S3Template
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.core.io.Resource

@Component
@ConditionalOnProperty(prefix = "helpfiles", name = ["cloud-provider"], havingValue = "aws", matchIfMissing = false)
class AwsObjectStorage(
    private val s3: S3Template,
    private val props: HelpResourceProperties
) : ObjectStorage {

    override fun list(prefix: String): List<String> =
        s3.listObjects(props.bucketName, prefix).map { it.filename }

    override fun get(key: String): Resource =
        s3.download(props.bucketName, key)
}
