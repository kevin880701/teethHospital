package com.lhr.teethHospital.net.request

import okhttp3.MultipartBody

data class UploadRequest(
    val image: MultipartBody.Part
)
