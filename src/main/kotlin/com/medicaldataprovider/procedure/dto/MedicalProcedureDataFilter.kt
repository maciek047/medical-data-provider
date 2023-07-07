package com.medicaldataprovider.procedure.dto

import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant

data class MedicalProcedureDataFilter (
    @RequestParam val categoryIds: String? = null,
    @RequestParam val procedureIds: String? = null,
    @RequestParam val diagnosisIds: String? = null,
    @RequestParam val startDate: Instant? = null,
    @RequestParam val endDate: Instant? = null,
)
