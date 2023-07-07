package com.medicaldataprovider.procedure

import com.medicaldataprovider.procedure.dto.MedicalProcedureDataResponse
import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordCountStackDto
import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordDto
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
import com.medicaldataprovider.procedure.util.toLocalDateTime
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

        val recordsInTimeIncrements = records.sliceByTimeIncrements(timeIncrement)
        val procedureRecordStacks = recordsInTimeIncrements.stackRecordsByProperty(stackType)

        val recalculatedRecordStacks = recalculateRecordStacks(valueType, procedureRecordStacks)

        return MedicalProcedureDataResponse(
            timeIncrement = timeIncrement,
            recordStackType = stackType,
            procedureRecordValueType = valueType,
            procedureRecordStacks = recalculatedRecordStacks
        )
    }

    private fun recalculateRecordStacks(
        valueType: ProcedureRecordValueType,
        stacks: List<MedicalProcedureRecordCountStackDto>
    ): List<MedicalProcedureRecordCountStackDto> {
        return when (valueType) {
            ProcedureRecordValueType.COUNT -> stacks
            ProcedureRecordValueType.PERCENTAGE -> stacks.recalculateValuesAsPercentageShare()
            ProcedureRecordValueType.PERCENTAGE_GROWTH -> stacks.recalculateValuesAsPercentageGrowth()
        }
    }

    private fun List<MedicalProcedureRecordCountStackDto>.recalculateValuesAsPercentageShare(): List<MedicalProcedureRecordCountStackDto> {
        val recalculatedStackedRecords = mutableListOf<MedicalProcedureRecordCountStackDto>()
        forEach { stack ->
            val recalculatedRecords = mutableListOf<MedicalProcedureRecordDto>()
            val total = stack.records.sumOf { record -> record.value }
            stack.records.forEach { record ->
                val recalculatedValue = if (total != 0.0) record.value / total else 0.0

                recalculatedRecords.add(
                    MedicalProcedureRecordDto(
                        propertyName = record.propertyName,
                        value = recalculatedValue
                    )
                )
            }
            recalculatedStackedRecords.add(
                MedicalProcedureRecordCountStackDto(
                    records = recalculatedRecords,
                    timePoint = stack.timePoint
                )
            )
        }
        return recalculatedStackedRecords
    }

    private fun List<MedicalProcedureRecordCountStackDto>.recalculateValuesAsPercentageGrowth(): List<MedicalProcedureRecordCountStackDto> {
        val recalculatedStackedRecords = mutableListOf<MedicalProcedureRecordCountStackDto>()
        val previousValues = mutableMapOf<String, Double>()
        forEach { stack ->
            val recalculatedRecords = mutableListOf<MedicalProcedureRecordDto>()
            stack.records.forEach { record ->
                val previousValue = previousValues[record.propertyName]
                val recalculatedValue = previousValue?.let {
                    if (it != 0.0) (record.value - previousValue) / previousValue else 1.0
                } ?: 0.0

                recalculatedRecords.add(
                    MedicalProcedureRecordDto(
                        propertyName = record.propertyName,
                        value = recalculatedValue
                    )
                )
                previousValues[record.propertyName] = record.value
            }
            recalculatedStackedRecords.add(
                MedicalProcedureRecordCountStackDto(
                    records = recalculatedRecords,
                    timePoint = stack.timePoint
                )
            )
        }
        return recalculatedStackedRecords
    }

    private fun List<MedicalProcedureRecord>.sliceByTimeIncrements(timeIncrement: TimeIncrement) =
        when (timeIncrement) {
            MONTH -> this.groupBy { "${it.timeStamp.toLocalDateTime().monthValue}-${it.timeStamp.toLocalDateTime().year}" }
            QUARTER -> this.groupBy { "Q${(it.timeStamp.toLocalDateTime().monthValue / 3 + 1)}-${it.timeStamp.toLocalDateTime().monthValue}-${it.timeStamp.toLocalDateTime().year}" }
            YEAR -> this.groupBy { "${it.timeStamp.toLocalDateTime().year}" }
        }

    private fun Map<String, List<MedicalProcedureRecord>>.stackRecordsByProperty(
        stackType: ProcedureRecordStackType,
    ): List<MedicalProcedureRecordCountStackDto> =
        map { (timeKey, record) ->
            val stacks = when (stackType) {
                PROCEDURE -> record.groupBy { it.procedureId }
                DIAGNOSIS -> record.groupBy { it.diagnosisId }
                CATEGORY -> record.groupBy { it.categoryId }
                NONE -> mapOf(UUID.randomUUID() to record)
            }

            MedicalProcedureRecordCountStackDto(
                records = stacks.toMedicalProcedureRecordDtoList(),
                timePoint = timeKey
            )
        }

    private fun Map<UUID, List<MedicalProcedureRecord>>.toMedicalProcedureRecordDtoList(): List<MedicalProcedureRecordDto> =
        map {
            val propertyId = it.key
            val recordList = it.value
            MedicalProcedureRecordDto(propertyId.toString(), recordList.size.toDouble())
        }.toList()
}
