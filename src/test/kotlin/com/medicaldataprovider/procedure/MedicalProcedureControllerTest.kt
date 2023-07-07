package com.medicaldataprovider.procedure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.medicaldataprovider.procedure.dto.MedicalProcedureDataFilter
import com.medicaldataprovider.procedure.dto.MedicalProcedureDataRequest
import com.medicaldataprovider.procedure.dto.MedicalProcedureDataResponse
import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordCountStackDto
import com.medicaldataprovider.procedure.dto.MedicalProcedureRecordDto
import com.medicaldataprovider.procedure.model.MedicalProcedureRecord
import com.medicaldataprovider.procedure.model.ProcedureRecordStackType.PROCEDURE
import com.medicaldataprovider.procedure.model.ProcedureRecordValueType.COUNT
import com.medicaldataprovider.procedure.model.TimeIncrement.MONTH
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class)
class MedicalProcedureControllerTest {

    @MockBean
    private lateinit var medicalProcedureService: MedicalProcedureService

    @MockBean
    private lateinit var assemblyService: MedicalProcedureDataAssemblyService

    @Test
    fun `should get medical procedure data`() {
        val request = MedicalProcedureDataRequest(
            timeIncrement = MONTH,
            recordStackType = PROCEDURE,
            procedureRecordValueType = COUNT,
            filter = MedicalProcedureDataFilter(
                categoryIds = "${UUID.randomUUID()},${UUID.randomUUID()}",
                procedureIds = "${UUID.randomUUID()},${UUID.randomUUID()}",
                diagnosisIds = "${UUID.randomUUID()},${UUID.randomUUID()}",
                startDate = Instant.now().minusSeconds(3600),
                endDate = Instant.now()
            )
        )

        val mockRecords = listOf(
            MedicalProcedureRecord(
                id = UUID.randomUUID(),
                procedureId = UUID.randomUUID(),
                diagnosisId = UUID.randomUUID(),
                categoryId = UUID.randomUUID(),
                timeStamp = Instant.now()
            )
        )

        val expectedResponse = MedicalProcedureDataResponse(
            timeIncrement = request.timeIncrement,
            recordStackType = request.recordStackType,
            procedureRecordValueType = request.procedureRecordValueType,
            procedureRecordStacks = listOf(
                MedicalProcedureRecordCountStackDto(
                    records = listOf(
                        MedicalProcedureRecordDto(
                            propertyName = "Mock Procedure",
                            value = 10.0
                        )
                    ),
                    timePoint = "2023-07"
                )
            )
        )

        `when`(medicalProcedureService.fetchMedicalProcedureRecords(request.filter)).thenReturn(mockRecords)

        `when`(assemblyService.assembleResults(
            mockRecords,
            request.timeIncrement,
            request.recordStackType,
            request.procedureRecordValueType
        )).thenReturn(expectedResponse)

        val server = WireMockServer()
        server.stubFor(
            get(urlEqualTo("/api/medical-procedure-data"))
                .withQueryParam("timeIncrement", equalTo(request.timeIncrement.value))
                .withQueryParam("recordStackType", equalTo(request.recordStackType.value))
                .withQueryParam("procedureRecordValueType", equalTo(request.procedureRecordValueType.value))
                .withQueryParam("filter.categoryIds", equalTo(request.filter.categoryIds))
                .withQueryParam("filter.procedureIds", equalTo(request.filter.procedureIds))
                .withQueryParam("filter.diagnosisIds", equalTo(request.filter.diagnosisIds))
                .withQueryParam("filter.startDate", equalTo(request.filter.startDate.toString()))
                .withQueryParam("filter.endDate", equalTo(request.filter.endDate.toString()))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(Json.encodeToString(expectedResponse))
                )
        )
    }
}
