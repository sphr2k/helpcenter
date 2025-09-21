package io.bootify.helpcenter.help

import java.net.URI
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/help-resources")
class HelpStreamController(
        private val props: HelpResourceProperties,
        private val signer: ObjectUrlSigner
) {
    @GetMapping("/{name}/{type}/stream")
    fun stream(@PathVariable name: String, @PathVariable type: String): ResponseEntity<Void> {
        val normalizedType = type.trim().lowercase()
        val finalName = "$name.$normalizedType"
        val key = props.bucketPrefix + finalName

        val contentType =
                MediaTypeFactory.getMediaType(finalName)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM)
                        .toString()

        val signed = signer.signGetUrl(key, contentType)
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(signed)).build()
    }
}
