package com.medicaldataprovider.procedure

import com.medicaldataprovider.procedure.dto.MedicalProcedureDataRequest
import com.medicaldataprovider.procedure.dto.MedicalProcedureDataResponse
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api")
class MedicalProcedureController(
    private val medicalProcedureService: MedicalProcedureService,
    private val assemblyService: MedicalProcedureDataAssemblyService
) {
    @PostMapping("/medical-procedures")
    fun fetchMedicalProcedureData(@RequestBody request: MedicalProcedureDataRequest): MedicalProcedureDataResponse {

        val medicalProcedureRecords = medicalProcedureService.fetchMedicalProcedureRecords(filter = request.filter)

        return assemblyService.assembleResults(
            medicalProcedureRecords,
            request.timeIncrement,
            request.recordStackType,
            request.procedureRecordValueType
        )
    }

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: RuntimeException): String {
        return "An error occurred: ${e.message}"
    }
}
