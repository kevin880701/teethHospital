package com.lhr.teethHospital.net

import com.lhr.teethHospital.net.request.TestPostRequest
import com.lhr.teethHospital.net.response.toUploadResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class NetManager() {

    // 創建 Retrofit 實例
    val retrofit = Retrofit.Builder()
        .baseUrl(" https://48d3-163-17-136-120.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    // 创建 ApiService 实例
    val apiService = retrofit.create(ApiService::class.java)



    fun testPost() {
        val testPostRequest = TestPostRequest("Received data: Hello, Flask!")
        apiService.testPost(testPostRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        val message = responseData.message
                        val data = responseData.data
                        println("###########################")
                        println("message：$message")
                        println("data：${data.toString()}")
                        var uploadResponse = data.toUploadResponse()
                        println("teethRangePath：${uploadResponse.teethRangePath}")
                        println("###########################")
                        // 处理响应数据
                    }
                } else {
                    println("###########################")
                    println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE")
                    println("###########################")
                    // 处理请求失败
                }
            }, { error ->
                println("testPost ERROR：" + error)
                // 处理网络请求错误
            })
    }


    fun uploadImage(image: RequestBody) {
//        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
//        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

//        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val params = HashMap<String, RequestBody>()
//        params["file"] = requestBody
        apiService.uploadImage(image)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        val message = responseData.message
                        val data = responseData.data
                        println("###########################")
                        println("message：$message")
                        println("data：${data.toString()}")
                        var uploadResponse = data.toUploadResponse()
                        println("teethRangePath：${uploadResponse.teethRangePath}")
                        println("teethRangePath：${uploadResponse.teethRangeDetectPath}")
                        println("###########################")
                        // 处理响应数据
                    }
                } else {
                    println("###########################")
                    println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE")
                    println("###########################")
                    // 处理请求失败
                }
            }, { error ->
                // 处理网络请求错误
            })
    }
}