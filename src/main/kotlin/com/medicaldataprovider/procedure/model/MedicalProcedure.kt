package com.medicaldataprovider.procedure.model

import java.util.UUID

data class MedicalProcedure (
    val id: UUID,
    val name: String,
    val categoryId: UUID,
    val count: Int,
)
