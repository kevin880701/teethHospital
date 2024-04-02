package com.lhr.teethHospital.net

import com.lhr.teethHospital.net.request.TestPostRequest
import com.lhr.teethHospital.net.response.BaseResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @Multipart
    @POST("upload")
    fun uploadImage(@Part("file\"; filename=\"image.jpg\"") file: RequestBody): Observable<Response<BaseResponse>>

    @POST("testPost")
    fun testPost(@Body testPostRequest: TestPostRequest): Observable<Response<BaseResponse>>
}

data class DataModel(val id: Int, val name: String)