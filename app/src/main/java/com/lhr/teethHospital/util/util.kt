package com.lhr.teethHospital.util

import android.content.Context
import android.os.Environment
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