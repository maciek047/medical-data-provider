package com.medicaldataprovider.procedure.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun String.splitToUUIDList(): List<UUID> = this.split(",").map { UUID.fromString(it) }

fun Instant.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
