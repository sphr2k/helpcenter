package io.bootify.helpcenter.help

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("helpfiles")
data class HelpResourceProperties(
    val cloudProvider: CloudProvider = CloudProvider.AWS,
    val bucketName: String,
    val bucketPrefix: String = "files/"
)

enum class CloudProvider { AWS, GCP }