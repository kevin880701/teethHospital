package com.lhr.teethHospital.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Throws(IOException::class)
fun createImageFile(context: Context): File {
    // 创建一个唯一的文件名，以确保不会与其他文件冲突
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    // 创建临时文件
    return File.createTempFile(
        imageFileName,  /* 前缀 */
        ".jpg",  /* 后缀 */
        storageDir /* 存储目录 */
    )
}

fun loadImageAsBitmap(url: String, context: Context): Bitmap? {
    println("IMAGE URL：${url}")
    return Glide.with(context)
        .asBitmap()
        .load(url)
        .submit()
        .get()
}

// 扩展函数，将 Bitmap 转换为 RequestBody
fun Bitmap.toRequestBody(): RequestBody {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    return object : RequestBody() {
        override fun contentType() = "image/*".toMediaTypeOrNull()
        override fun writeTo(sink: BufferedSink) {
            try {
                sink.write(byteArray)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}