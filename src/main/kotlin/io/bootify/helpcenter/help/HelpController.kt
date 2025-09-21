package io.bootify.helpcenter.rest

import io.bootify.helpcenter.help.HelpResourceProperties
import io.bootify.helpcenter.help.ObjectStorage
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/")
class HelpController(
    private val props: HelpResourceProperties,
    private val storage: ObjectStorage
) {

    @GetMapping
    fun list(): List<Map<String, String>> =
        storage.list(props.bucketPrefix)
            .filter { it.isNotBlank() && !it.endsWith("/") }
            .map { name ->
                val fileName = name.substringAfterLast('/')
                val urlName = UriUtils.encodePathSegment(fileName, StandardCharsets.UTF_8)
                val url = "/file/$urlName"
                mapOf("name" to fileName, "url" to url)
            }

    @GetMapping("/file/{filename}")
    fun get(@PathVariable filename: String): ResponseEntity<Resource> {
        val resource = storage.get(props.bucketPrefix + filename)
        if (!resource.exists()) return ResponseEntity.notFound().build()

        val mediaType = MediaTypeFactory
            .getMediaType(resource.filename ?: "")
            .orElse(MediaType.APPLICATION_OCTET_STREAM)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(resource)
    }
}
