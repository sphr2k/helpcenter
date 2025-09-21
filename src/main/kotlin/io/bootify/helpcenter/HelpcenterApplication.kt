package io.bootify.helpcenter

import io.bootify.helpcenter.help.HelpResourceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(HelpResourceProperties::class)
class HelpcenterApplication

fun main(args: Array<String>) {
    runApplication<HelpcenterApplication>(*args)
}
