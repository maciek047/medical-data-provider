package com.medicaldataprovider.procedure.dto

import com.medicaldataprovider.procedure.model.ProcedureRecordStackType
import com.medicaldataprovider.procedure.model.ProcedureRecordValueType
import com.medicaldataprovider.procedure.model.TimeIncrement
import org.springframework.web.bind.annotation.RequestParam

data class MedicalProcedureDataRequest (
    @RequestParam val timeIncrement: TimeIncrement,
    @RequestParam val recordStackType: ProcedureRecordStackType,
    @RequestParam val procedureRecordValueType: ProcedureRecordValueType,
    @RequestParam val filter: MedicalProcedureDataFilter
)
