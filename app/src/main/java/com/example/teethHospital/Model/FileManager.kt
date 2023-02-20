package com.example.teethHospital.Model

import android.util.Log
import com.example.teethHospital.Model.Model.Companion.PICTURE_DIR
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.Room.RecordEntity
import com.example.teethHospital.Room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class FileManager {
    val dataBase = SqlDatabase(mainActivity)

    fun deleteDirectory(dir: String,recordEntity: RecordEntity): Boolean {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        var dir = dir
        if (!dir.endsWith(File.separator)) dir += File.separator
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            println("刪除目錄失敗：" + dir + "不存在！")
            return false
        }
        var flag = true
        // 刪除文件夾中的所有文件包括子目錄
        val files: Array<File> = dirFile.listFiles()
        for (i in files.indices) {
            // 刪除子文件
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag) break
            } else if (files[i].isDirectory) {
                flag = deleteDirectory(
                    files[i]
                        .absolutePath,recordEntity
                )
                if (!flag) break
            }
        }

        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                dataBase.getRecordDao().deleteRecord(recordEntity.fileName)
            }
        }

        if (!flag) {
            println("刪除目錄失敗！")
            return false
        }
        // 刪除當前目錄
        return if (dirFile.delete()) {
            println("刪除目錄" + dir + "成功！")
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
                println("刪除單個文件" + fileName + "成功！")
                true
            } else {
                println("刪除單個文件" + fileName + "失敗！")
                false
            }
        } else {
            println("刪除單個文件失敗：" + fileName + "不存在！")
            false
        }
    }

    fun updateFileName(oldHospitalName: String, oldNumber: String, newHospitalName: String, newNumber: String) {
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                var recordEntityList = dataBase.getRecordDao().selectUpdateFileName(oldHospitalName, oldNumber) as ArrayList<RecordEntity>
                for (recordEntity in recordEntityList) {
                    File(PICTURE_DIR + recordEntity.fileName).renameTo(File(PICTURE_DIR + newHospitalName + newNumber + recordEntity.recordDate))
                    dataBase.getRecordDao().updateFileName(recordEntity.fileName, newHospitalName + newNumber + recordEntity.recordDate)
                }
            }
        }
    }
}