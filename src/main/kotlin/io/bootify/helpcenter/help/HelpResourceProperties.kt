package io.bootify.helpcenter.help

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("helpfiles")
data class HelpResourceProperties(
    val cloudProvider: CloudProvider = CloudProvider.AWS,
    val bucketName: String,
    val bucketPrefix: String = "files/",
    val signedUrlTtl: java.time.Duration = java.time.Duration.ofMinutes(15),
)

enum class CloudProvider { AWS, GCP }
