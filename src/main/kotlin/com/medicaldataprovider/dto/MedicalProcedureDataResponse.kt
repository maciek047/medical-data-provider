package com.medicaldataprovider.dto

import com.medicaldataprovider.model.ProcedureRecordStackType
import com.medicaldataprovider.model.ProcedureRecordValueType
import com.medicaldataprovider.model.TimeIncrement

data class MedicalProcedureDataResponse (
    val timeIncrement: TimeIncrement,
    val recordStackType: ProcedureRecordStackType,
    val procedureRecordValueType: ProcedureRecordValueType,
    val procedureRecordStacks: List<MedicalProcedureRecordCountStackDto>
)

data class MedicalProcedureRecordCountStackDto (
        val records: List<MedicalProcedureRecordDto>,
        val timePoint: String
)

data class MedicalProcedureRecordDto (
    val propertyName: String,  // this can be procedureName, diagnosisName or categoryName, depending on requested recordStackType
    val value: Double, // this can be count, percentage or percentage growth, depending on requested procedureRecordValueType
)
