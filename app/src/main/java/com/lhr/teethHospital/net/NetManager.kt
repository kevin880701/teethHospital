package com.lhr.teethHospital.net

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class NetManager() {

    // 創建 Retrofit 實例
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    // 创建 ApiService 实例
    val apiService = retrofit.create(ApiService::class.java)



    fun sendData() {
        val requestBody = "Your request body".toRequestBody("application/json".toMediaTypeOrNull())
        apiService.upload(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    // 处理请求成功
                } else {
                    // 处理请求失败
                }
            }, { error ->
                // 处理网络请求错误
            })
    }
}