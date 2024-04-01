package com.lhr.teethHospital.net.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class UploadResponse(
    var teethRangePath: String,
    var teethRangeDetectPath: String
) {

    fun UploadResponse.toJsonString(): String {
        var objectMapper = jacksonObjectMapper()

        var uploadResponseMap = mutableMapOf<String, Any?>(
            "teethRangePath" to teethRangePath,
            "teethRangeDetectPath" to teethRangeDetectPath
        )
        return objectMapper.writeValueAsString(uploadResponseMap)
    }
}

fun String.toUploadResponse(): UploadResponse {
    val objectMapper = jacksonObjectMapper()
    val uploadResponseMap: Map<String, Any?> = objectMapper.readValue(this)

    val teethRangePath = uploadResponseMap["teethRangePath"] as? String ?: ""
    val teethRangeDetectPath = uploadResponseMap["teethRangeDetectPath"] as? String ?: ""

    return UploadResponse(teethRangePath, teethRangeDetectPath)
}

fun Map<String, Any?>.toUploadResponse(): UploadResponse {
    val teethRangePath = this["teethRangePath"] as? String ?: ""
    val teethRangeDetectPath = this["teethRangeDetectPath"] as? String ?: ""

    return UploadResponse(teethRangePath, teethRangeDetectPath)
}