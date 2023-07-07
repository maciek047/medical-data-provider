package com.medicaldataprovider.procedure

import com.medicaldataprovider.dto.MedicalProcedureDataResponse
import com.medicaldataprovider.model.ProcedureRecordStackType
import com.medicaldataprovider.model.ProcedureRecordValueType
import com.medicaldataprovider.model.TimeIncrement
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api")
class MedicalProcedureController(
    private val medicalProcedureService: MedicalProcedureService,
    private val assemblyService: MedicalProcedureDataAssemblyService
) {
    @GetMapping("/medical-procedure-data")
    fun getMedicalProcedureData(request: MedicalProcedureDataRequest): ResponseEntity<MedicalProcedureDataResponse> {

        val medicalProcedureRecords = medicalProcedureService.getMedicalProcedureRecords(
            startDate = request.filter.startDate,
            endDate = request.filter.endDate,
            procedureIds = request.filter.procedureIds?.splitToUUIDList(),
            diagnosisIds = request.filter.diagnosisIds?.splitToUUIDList(),
            categoryIds = request.filter.categoryIds?.splitToUUIDList()
        )

        val medicalProcedureDataResponse = assemblyService.assembleResults(
            medicalProcedureRecords,
            request.timeIncrement,
            request.recordStackType,
            request.procedureRecordValueType
        )

        return ResponseEntity.ok(medicalProcedureDataResponse)
    }
}


data class MedicalProcedureDataRequest (
    @RequestParam val timeIncrement: TimeIncrement,
    @RequestParam val recordStackType: ProcedureRecordStackType,
    @RequestParam val procedureRecordValueType: ProcedureRecordValueType,
    @RequestParam val filter: MedicalProcedureDataFilter
)


data class MedicalProcedureDataFilter (
    @RequestParam val categoryIds: String? = null,
    @RequestParam val procedureIds: String? = null,
    @RequestParam val diagnosisIds: String? = null,
    @RequestParam val startDate: Instant? = null,
    @RequestParam val endDate: Instant? = null,
)

fun String.splitToUUIDList(): List<UUID> = this.split(",").map { UUID.fromString(it) }
