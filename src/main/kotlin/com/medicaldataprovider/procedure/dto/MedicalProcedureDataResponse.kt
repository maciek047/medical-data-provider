package com.medicaldataprovider.procedure.dto

import com.medicaldataprovider.procedure.model.ProcedureRecordStackType
import com.medicaldataprovider.procedure.model.ProcedureRecordValueType
import com.medicaldataprovider.procedure.model.TimeIncrement
import kotlinx.serialization.Serializable

@Serializable
data class MedicalProcedureDataResponse (
    val timeIncrement: TimeIncrement,
    val recordStackType: ProcedureRecordStackType,
    val procedureRecordValueType: ProcedureRecordValueType,
    val procedureRecordStacks: List<MedicalProcedureRecordCountStackDto>
)

@Serializable
data class MedicalProcedureRecordCountStackDto (
        val records: List<MedicalProcedureRecordDto>,
        val timePoint: String
)

@Serializable
data class MedicalProcedureRecordDto (
    val propertyName: String,  // this can be procedureName, diagnosisName or categoryName, depending on requested recordStackType
    val value: Double, // this can be count, percentage or percentage growth, depending on requested procedureRecordValueType
)
