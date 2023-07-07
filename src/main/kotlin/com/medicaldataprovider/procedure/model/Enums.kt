package com.medicaldataprovider.procedure.model

enum class MedicalProcedureCategory(val value: String) {
    SURGERY("diagnosis"),
    EXAMINATION("procedure"),
    CATEGORY("category"),
    NONE("none"),
}

enum class TimeIncrement(val value: String) {
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year")
}

enum class ProcedureRecordValueType(val value: String) {
    COUNT("count"),
    PERCENTAGE("percentage"),
    PERCENTAGE_GROWTH("percentage_growth")
}

enum class ProcedureRecordStackType(val value: String) {
    PROCEDURE("procedure"),
    DIAGNOSIS("diagnosis"),
    CATEGORY("category"),
    NONE("none"),
}
