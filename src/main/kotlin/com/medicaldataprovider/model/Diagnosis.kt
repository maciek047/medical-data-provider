package com.medicaldataprovider.model

import java.util.UUID

data class Diagnosis(
    val id: UUID,
    val name: String,
    val description: String,
)
