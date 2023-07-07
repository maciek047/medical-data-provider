package com.medicaldataprovider.procedure.model

import java.util.UUID

data class Diagnosis(
    val id: UUID,
    val name: String,
    val description: String,
)
