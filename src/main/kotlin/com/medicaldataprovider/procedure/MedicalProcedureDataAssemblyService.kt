package com.medicaldataprovider.procedure

import com.medicaldataprovider.procedure.dto.MedicalProcedureDataResponse
import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordCountStackDto
import com.medicaldataprovider.procedure.model.MedicalProcedureRecord
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType.DIAGNOSIS
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType.PROCEDURE
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType.CATEGORY
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType.NONE
import com.medicaldataprovider.procedure.model.ProcedureRecordValueType
import com.medicaldataprovider.procedure.model.TimeIncrement
import com.medicaldataprovider.procedure.model.TimeIncrement.MONTH
import com.medicaldataprovider.procedure.model.TimeIncrement.QUARTER
import com.medicaldataprovider.procedure.model.TimeIncrement.YEAR
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MedicalProcedureDataAssemblyService {
    fun assembleResults(
        records: List<MedicalProcedureRecord>,
        timeIncrement: TimeIncrement,
        stackType: ProcedureRecordStackType,
        valueType: ProcedureRecordValueType
    ): MedicalProcedureDataResponse {

        //todo also unnest the below steps into separate functions
        val recordsInTimeIncrements = when (timeIncrement) {
            MONTH -> records.groupBy { "${it.timeStamp.toLocalDateTime().monthValue}-${it.timeStamp.toLocalDateTime().year}" }
            QUARTER -> records.groupBy { "Q${(it.timeStamp.toLocalDateTime().monthValue / 3 + 1)}-${it.timeStamp.toLocalDateTime().monthValue}-${it.timeStamp.toLocalDateTime().year}" }
            YEAR -> records.groupBy { "${it.timeStamp.toLocalDateTime().year}" }
        }

        val procedureRecordStacks = recordsInTimeIncrements.map { (timeKey, record) ->
            val recordsStacked = when (stackType) {
                PROCEDURE -> record.groupBy { it.procedureId }
                DIAGNOSIS -> record.groupBy { it.diagnosisId }
                CATEGORY -> record.groupBy { it.categoryId }
                NONE -> mapOf(UUID.randomUUID() to record)
            }

            MedicalProcedureRecordCountStackDto(
                records = recordsStacked.toMedicalProcedureRecordDtoList(),
                timePoint = timeKey
            )
        }

        //todo handle different value types (calculations) %share, %change, &count = sum
        return MedicalProcedureDataResponse(
            timeIncrement = timeIncrement,
            recordStackType = stackType,
            procedureRecordValueType = valueType,
            procedureRecordStacks = procedureRecordStacks
        )
    }
}
