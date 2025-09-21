package io.bootify.helpcenter.help

import io.bootify.helpcenter.help.MetricService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriUtils
import java.net.URI
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/help-resources")
class HelpResourceController(
    private val props: HelpResourceProperties,
    private val storage: ObjectStorage,
    private val signer: ObjectUrlSigner,
    private val metricService: MetricService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("", "/")
    fun list(): ResponseEntity<Any> =
        try {
            val items =
                storage
                    .list(props.bucketPrefix)
                    .mapNotNull { key ->
                        val file = java.io.File(key)
                        val name = file.nameWithoutExtension
                        val ext = file.extension
                        if (name.isNotBlank() && ext.isNotBlank()) {
                            mapOf("name" to name, "type" to ext)
                        } else {
                            null
                        }
                    }
            ResponseEntity.ok(items)
        } catch (ex: Exception) {
            log.error("Help list failed", ex)
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(mapOf("code" to "HELP_STORAGE_UNAVAILABLE", "message" to "Help resources are unavailable."))
        }

    @GetMapping("/{name}/{type}", "/{name}/{type}/")
    fun getFile(
        @PathVariable name: String,
        @PathVariable type: String,
    ): Map<String, String> {
        val encodedName = UriUtils.encodePathSegment(name, StandardCharsets.UTF_8)
        val url = "/help-resources/$encodedName/$type/stream"
        metricService.incrementGetHelpResource("$name.$type")
        return mapOf("url" to url)
    }

    @GetMapping("/{name}/{type}/stream", "/{name}/{type}/stream/")
    fun stream(
        @PathVariable name: String,
        @PathVariable type: String,
    ): ResponseEntity<Any> =
        try {
            val normalizedType = type.trim().lowercase()
            val finalName = "$name.$normalizedType"
            val key = props.bucketPrefix + finalName
            val contentType =
                MediaTypeFactory
                    .getMediaType(finalName)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM)
                    .toString()
            val signed = signer.signGetUrl(key, contentType)
            ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(signed))
                .build()
        } catch (ex: Exception) {
            log.error("Help stream failed for {}/{}", name, type, ex)
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(mapOf("code" to "HELP_STORAGE_UNAVAILABLE", "message" to "Help resources are unavailable."))
        }
}
