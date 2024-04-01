package com.lhr.teethHospital.net

import io.reactivex.rxjava3.core.Single
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @GET("endpoint")
    fun getData(): Single<Response<DataModel>>

    @POST("upload")
    fun upload(@Body requestBody: RequestBody): Single<Response<Void>>
}
data class DataModel(val id: Int, val name: String)