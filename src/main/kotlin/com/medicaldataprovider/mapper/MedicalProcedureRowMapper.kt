package com.medicaldataprovider.mapper

import com.medicaldataprovider.model.MedicalProcedureRecord
import java.sql.ResultSet
import java.util.UUID
import org.springframework.jdbc.core.RowMapper
class MedicalProcedureRowMapper : RowMapper<MedicalProcedureRecord> {
    override fun mapRow(rs: ResultSet, rowNum: Int): MedicalProcedureRecord {
        return MedicalProcedureRecord(
            id = UUID.fromString(rs.getString("id")),
            procedureId = UUID.fromString(rs.getString("procedure_id")),
            diagnosisId = UUID.fromString(rs.getString("diagnosis_id")),
            categoryId = UUID.fromString(rs.getString("category_id")),
            timeStamp = rs.getTimestamp("time_stamp").toInstant()
        )
    }
}
