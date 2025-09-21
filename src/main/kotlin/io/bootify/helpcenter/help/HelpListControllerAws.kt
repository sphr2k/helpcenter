// package io.bootify.helpcenter.help

// import java.net.URLEncoder
// import java.nio.charset.StandardCharsets
// import org.springframework.core.io.Resource
// import org.springframework.core.io.support.ResourcePatternResolver
// import org.springframework.http.HttpHeaders
// import org.springframework.http.MediaType
// import org.springframework.http.MediaTypeFactory
// import org.springframework.http.ResponseEntity
// import org.springframework.web.bind.annotation.GetMapping
// import org.springframework.web.bind.annotation.PathVariable
// import org.springframework.web.bind.annotation.RequestMapping
// import org.springframework.web.bind.annotation.RestController

// @RestController
// @RequestMapping("/")
// class HelpListControllerAws(
//         private val props: HelpResourceProperties,
//         private val resolver: ResourcePatternResolver
// ) {

//     @GetMapping
//     fun list(): List<Map<String, String>> {
//         val pattern = "s3://${props.bucketName}/${props.bucketPrefix}*"
//         val resources = resolver.getResources(pattern)
//         return resources
//                 .filter { it.exists() }
//                 .filter { res ->
//                     val name = res.filename ?: return@filter false
//                     name.isNotBlank() && !name.endsWith("/")
//                 }
//                 .mapNotNull { res ->
//                     val filename = res.filename ?: return@mapNotNull null
//                     val urlName = URLEncoder.encode(filename, StandardCharsets.UTF_8)
//                     mapOf("name" to filename, "url" to "/file/$urlName")
//                 }
//     }

//     @GetMapping("/file/{filename}")
//     fun get(@PathVariable filename: String): ResponseEntity<Resource> {
//         val resource =
//                 resolver.getResource("s3://${props.bucketName}/${props.bucketPrefix}$filename")
//         if (!resource.exists()) return ResponseEntity.notFound().build()

//         val mediaType: MediaType =
//                 MediaTypeFactory.getMediaType(resource.filename ?: "")
//                         .orElse(MediaType.APPLICATION_OCTET_STREAM)

//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
//                 .body(resource)
//     }
// }
