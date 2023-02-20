package com.lhr.teethHospital.Room

import android.content.Context
import android.net.Uri
import android.util.Log
import com.lhr.teethHospital.Model.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.properties.Delegates

class CsvToSql {
    var dataBase: SqlDatabase

    //    val filepath = APP_FILES_PATH + File.separator + csvBackUp
    var linePosition = 0
    var maxRecyclerPosition by Delegates.notNull<Int>()

    constructor(mContext: Context, filepath: Uri) {
        dataBase = SqlDatabase(mContext)

        try {
            var fileInputStream = Model.mainActivity.contentResolver.openInputStream(filepath)
            val r = BufferedReader(InputStreamReader(fileInputStream))
            var mLine: String?
            while (r.readLine().also { mLine = it } != null) {
                if (linePosition > 0) {
                    var str = (mLine as String).split(",".toRegex())
                    var classEntity = HospitalEntity()
                    classEntity.hospitalName = str[0]
                    classEntity.number = str[1]
                    classEntity.gender = str[2]
                    classEntity.birthday = str[3]

                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
//                            dataBase.getClassDao().importCsv(classEntity)
                            dataBase.getHospitalDao().importPatientInformation(
                                classEntity.hospitalName,
                                classEntity.number,
                                classEntity.gender,
                                classEntity.birthday)
                        }
                    }
                }
                linePosition++
            }
        } catch (e: Exception) {
            Log.e("CsvToSqlError", "" + e)
        }
    }
}
