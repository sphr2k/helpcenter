package io.bootify.helpcenter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import io.bootify.helpcenter.help.HelpResourceProperties

@SpringBootApplication
@EnableConfigurationProperties(HelpResourceProperties::class)
class HelpcenterApplication

fun main(args: Array<String>) {
    runApplication<HelpcenterApplication>(*args)
    // System.getenv().forEach { (k, v) -> println("$k=$v") }
}
