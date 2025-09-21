package io.bootify.helpcenter.help

import io.awspring.cloud.s3.S3Template
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.S3Exception

@Component
@ConditionalOnProperty(prefix = "helpfiles", name = ["cloud-provider"], havingValue = "aws", matchIfMissing = false)
class AwsObjectStorage(
    private val s3: S3Template,
    private val props: HelpResourceProperties
) : ObjectStorage {

    override fun list(prefix: String): List<String> = try {
        s3.listObjects(props.bucketName, prefix)
            .mapNotNull { it.filename }
            .filter { it.isNotBlank() && !it.endsWith("/") }
            .map { it.removePrefix(prefix) }
    } catch (ex: S3Exception) {
        val code = if (ex.statusCode() == 403) "AWS_S3_FORBIDDEN" else "AWS_S3_ERROR"
        throw StorageAccessException(code, "S3 access failed for bucket ${props.bucketName}: ${ex.awsErrorDetails()?.errorMessage()}", ex)
    } catch (ex: Exception) {
        throw StorageAccessException("AWS_S3_ERROR", "S3 operation failed for bucket ${props.bucketName}", ex)
    }

    override fun get(key: String): Resource = try {
        s3.download(props.bucketName, key)
    } catch (ex: NoSuchKeyException) {
        throw StorageAccessException("AWS_S3_NOT_FOUND", "S3 object not found: $key", ex)
    } catch (ex: S3Exception) {
        val code = if (ex.statusCode() == 403) "AWS_S3_FORBIDDEN" else "AWS_S3_ERROR"
        throw StorageAccessException(code, "S3 object access failed: $key", ex)
    } catch (ex: Exception) {
        throw StorageAccessException("AWS_S3_ERROR", "S3 operation failed for key $key", ex)
    }
}
