package com.medicaldataprovider.procedure

import com.medicaldataprovider.procedure.dto.MedicalProcedureDataFilter
import com.medicaldataprovider.procedure.mapper.MedicalProcedureRowMapper
import com.medicaldataprovider.procedure.model.MedicalProcedureRecord
import com.medicaldataprovider.procedure.util.splitToUUIDList
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class MedicalProcedureService(jdbcTemplate: JdbcTemplate) {
    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    fun fetchMedicalProcedureRecords(filter: MedicalProcedureDataFilter): List<MedicalProcedureRecord> {
        val sql = buildSqlQuery(filter)
        val paramMap = buildSqlParameters(filter)

        return namedParameterJdbcTemplate.query(sql, paramMap, MedicalProcedureRowMapper())
    }

    private fun buildSqlQuery(filter: MedicalProcedureDataFilter): String {
        val sql = StringBuilder(
            """SELECT * FROM medical_procedure_record WHERE 1=1""" //fixme use query builder
        )

        filter.procedureIds?.let { sql.append(" AND procedure_id IN (:procedureIds)") }
        filter.diagnosisIds?.let { sql.append(" AND diagnosis_id IN (:diagnosisIds)") }
        filter.categoryIds?.let { sql.append(" AND category_id IN (:categoryIds)") }
        filter.startDate?.let { sql.append(" AND time_stamp >= :startDate") }
        filter.endDate?.let { sql.append(" AND time_stamp <= :endDate") }

        return sql.toString()
    }

    private fun buildSqlParameters(filter: MedicalProcedureDataFilter): MapSqlParameterSource {
        return MapSqlParameterSource()
            .addValue("procedureIds", filter.procedureIds?.splitToUUIDList())
            .addValue("diagnosisIds", filter.diagnosisIds?.splitToUUIDList())
            .addValue("categoryIds", filter.categoryIds?.splitToUUIDList())
            .addValue("startDate", filter.startDate)
            .addValue("endDate", filter.endDate)
    }
}
