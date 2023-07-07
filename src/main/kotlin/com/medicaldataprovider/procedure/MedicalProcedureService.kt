package com.medicaldataprovider.procedure

import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordDto
import com.medicaldataprovider.procedure.mapper.MedicalProcedureRowMapper
import com.medicaldataprovider.procedure.model.MedicalProcedureRecord
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Service
class MedicalProcedureService(private val jdbcTemplate: JdbcTemplate) {
    fun getMedicalProcedureRecords(
        startDate: Instant?,
        endDate: Instant?,
        procedureIds: List<UUID>?,
        diagnosisIds: List<UUID>?,
        categoryIds: List<UUID>?,
    ): List<MedicalProcedureRecord> {
        val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

        val sql = StringBuilder(
            """ SELECT * FROM medical_procedure_record WHERE 1=1 """ //fixme: use a query builder
        )

        procedureIds?.let {
            sql.append(" AND procedure_id IN (:procedureIds)")
        }

        diagnosisIds?.let {
            sql.append(" AND diagnosis_id IN (:diagnosisIds)")
        }

        categoryIds?.let {
            sql.append(" AND category_id IN (:categoryIds)")
        }

        startDate?.let {
            sql.append(" AND time_stamp >= :startDate")
        }
        endDate?.let {
            sql.append(" AND time_stamp <= :endDate")
        }

        val paramMap = MapSqlParameterSource()
            .addValue("procedureIds", procedureIds)
            .addValue("diagnosisIds", diagnosisIds)
            .addValue(
                "startDate",
                startDate?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) })
            .addValue("endDate", endDate?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) })

        return namedParameterJdbcTemplate.query(sql.toString(), paramMap, MedicalProcedureRowMapper())
    }
}

fun Map<UUID, List<MedicalProcedureRecord>>.toMedicalProcedureRecordDtoList(): List<MedicalProcedureRecordDto> =
    map {
        val propertyId = it.key
        val recordList = it.value
        MedicalProcedureRecordDto(propertyId.toString(), recordList.size.toDouble())
    }.toList()

fun Instant.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
