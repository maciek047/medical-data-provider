package com.medicaldataprovider.procedure.model

import java.time.Instant
import java.util.UUID

data class MedicalProcedureRecord (
    val id: UUID,
    val procedureId: UUID,
    val diagnosisId: UUID,
    val categoryId: UUID,
    val timeStamp: Instant,
)
