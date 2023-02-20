package com.lhr.teethHospital.util.DetectResult

import android.content.Intent
import android.util.Log
import com.lhr.teethHospital.Model.Model.Companion.CleanRecordFilter
import com.lhr.teethHospital.Model.Model.Companion.UPDATE_CLEAN_RECORD
import com.lhr.teethHospital.Room.RecordEntity
import com.lhr.teethHospital.Room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class DetectResultPresenter(detectResultActivity: DetectResultActivity) {
    var detectResultActivity = detectResultActivity
    val dataBase = SqlDatabase(detectResultActivity)

    fun deleteDirectory(dir: String,recordEntity: RecordEntity): Boolean {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        var dir = dir
        if (!dir.endsWith(File.separator)) dir += File.separator
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            println("删除目录失败：" + dir + "不存在！")
            return false
        }
        var flag = true
        // 删除文件夹中的所有文件包括子目录
        val files: Array<File> = dirFile.listFiles()
        for (i in files.indices) {
            // 删除子文件
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag) break
            } else if (files[i].isDirectory()) {
                flag = deleteDirectory(
                    files[i]
                        .absolutePath,recordEntity
                )
                if (!flag) break
            }
        }

        Log.v("OOOOOOOOOOOOOO","" + recordEntity.fileName)
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                dataBase.getRecordDao().deleteRecord(recordEntity.fileName)
            }
        }

        detectResultActivity.sendBroadcast(
            Intent(CleanRecordFilter).putExtra("action", UPDATE_CLEAN_RECORD)
        )

        if (!flag) {
            println("删除目录失败！")
            return false
        }
        // 删除当前目录
        return if (dirFile.delete()) {
            println("删除目录" + dir + "成功！")
            true
        } else {
            false
        }
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(fileName)
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        return if (file.exists() && file.isFile) {
            if (file.delete()) {
                println("删除单个文件" + fileName + "成功！")
                true
            } else {
                println("删除单个文件" + fileName + "失败！")
                false
            }
        } else {
            println("删除单个文件失败：" + fileName + "不存在！")
            false
        }
    }
}