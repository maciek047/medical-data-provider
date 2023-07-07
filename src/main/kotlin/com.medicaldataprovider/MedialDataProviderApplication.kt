package com.medicaldataprovider

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MedialDataProviderApplication

fun main(args: Array<String>) {
    runApplication<MedialDataProviderApplication>(*args)
}
