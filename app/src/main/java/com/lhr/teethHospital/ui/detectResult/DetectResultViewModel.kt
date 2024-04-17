package com.lhr.teethHospital.ui.detectResult

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class DetectResultViewModel(context: Context, var personalManagerRepository: PersonalManagerRepository) : BaseViewModel(context)  {

    fun deleteDirectory(dir: String, recordEntity: RecordEntity, activity: Activity): Boolean {
        // 如果dir不以文件分隔符結尾，自動添加文件分隔符
        var dir = dir
        if (!dir.endsWith(File.separator)) dir += File.separator
        val dirFile = File(dir)
        // 如果dir對應的文件不存在，或者不是一個目錄，則退出
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
            } else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].absolutePath, recordEntity, activity)
                if (!flag) break
            }
        }
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                SqlDatabase.getInstance().getRecordDao().deleteRecord(recordEntity.fileName)
            }
        }

        activity.sendBroadcast(
            Intent(Model.CleanRecordFilter).putExtra("action", Model.UPDATE_PATIENT_RECORD)
        )

        if (!flag) {
            println("刪除目錄失敗！")
            return false
        }
        // 删除当前目录
        return if (dirFile.delete()) {
            println("刪除目錄" + dir + "成功！")
            true
        } else {
            false
        }
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(fileName)
        // 如果文件路徑所對應的文件存在，並且是一個文件，則直接刪除
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
}