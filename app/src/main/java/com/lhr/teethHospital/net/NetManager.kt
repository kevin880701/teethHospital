package com.lhr.teethHospital.net

import android.content.Context
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.net.request.TestPostRequest
import com.lhr.teethHospital.net.response.toUploadResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class NetManager(context: Context) {

    companion object {
        private var instance: NetManager? = null
        fun getInstance(context: Context): NetManager {
            if (instance == null) {
                instance = NetManager(context)
            }
            return instance!!
        }
    }
    var repository = PersonalManagerRepository.getInstance(context)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 设置日志级别为 BODY，可以打印请求和响应的详细信息
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 添加日志拦截器
        .build()

    // 創建 Retrofit 實例
    val retrofit = Retrofit.Builder()
        .baseUrl(repository.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    // 创建 ApiService 实例
    val apiService = retrofit.create(ApiService::class.java)


    // 創建 Retrofit 實例
    val testRetrofit = Retrofit.Builder()
        .baseUrl(" https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    // 创建 ApiService 实例
    val testApiService = testRetrofit.create(ApiService::class.java)


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

    fun testGet(postId: Int) {
        testApiService.testGet(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        println("aAAAAAAAAAAAA")
                        println("" + responseData[0].body)
                        println("aAAAAAAAAAAAA")
                        // 处理响应数据
                    }
                } else {
                    // 处理请求失败
                    val errorBody = response.errorBody()?.string()
                    println("testGet ERROR：" + errorBody)
                }
            }, { error ->
                if (error is HttpException) {
                    // 处理网络请求错误
                    val errorBody = error.response()?.errorBody()?.string()
                    println("testGet ERROR：" + errorBody)
                } else {
                    println("testGet ERROR：" + error.message)
                }
            })
    }


    fun uploadImage(image: RequestBody) {
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


    fun getImage(imagePath: String) {
        testApiService.getImage(imagePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        println("aAAAAAAAAAAAA")
                        println("" + responseData)
                        println("aAAAAAAAAAAAA")
                        // 处理响应数据
                    }
                } else {
                    // 处理请求失败
                    val errorBody = response.errorBody()?.string()
                    println("testGet ERROR：" + errorBody)
                }
            }, { error ->
                if (error is HttpException) {
                    // 处理网络请求错误
                    val errorBody = error.response()?.errorBody()?.string()
                    println("testGet ERROR：" + errorBody)
                } else {
                    println("testGet ERROR：" + error.message)
                }
            })
    }
}