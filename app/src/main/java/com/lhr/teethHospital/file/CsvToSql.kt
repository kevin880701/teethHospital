package com.lhr.teethHospital.file

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.notesk.SQLite.SqlModel.Companion.afterPercent
import com.android.notesk.SQLite.SqlModel.Companion.beforePercent
import com.android.notesk.SQLite.SqlModel.Companion.birthday
import com.android.notesk.SQLite.SqlModel.Companion.fileName
import com.android.notesk.SQLite.SqlModel.Companion.gender
import com.android.notesk.SQLite.SqlModel.Companion.hospitalName
import com.android.notesk.SQLite.SqlModel.Companion.number
import com.android.notesk.SQLite.SqlModel.Companion.recordDate
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader

class CsvToSql {
    var linePosition = 0

    fun csvToHospitalSql(mContext: Context, filepath: Uri) {
        var dataBase = SqlDatabase(mContext)
        try {
            var fileInputStream = mContext.contentResolver.openInputStream(filepath)
            val r = BufferedReader(InputStreamReader(fileInputStream))
            var mLine: String?
            var hospitalNameIndex = 0
            var numberIndex = 0
            var genderIndex = 0
            var birthdayIndex = 0
            while (r.readLine().also { mLine = it } != null) {
                var str = (mLine as String).split(",".toRegex())
                if (linePosition == 0) {
                    for (index in str.indices) {
                        when (str[index]) {
                            mContext.getString(R.string.hospital) -> hospitalNameIndex = index
                            mContext.getString(R.string.patient_number) -> numberIndex = index
                            mContext.getString(R.string.gender) -> genderIndex = index
                            mContext.getString(R.string.birthday) -> birthdayIndex = index
                        }
                    }
                }
                Log.v("OOOOOO","" + hospitalNameIndex + "&" + numberIndex)
                if (linePosition > 0) {
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getHospitalDao().importPatientInformation(
                                str[hospitalNameIndex],
                                str[numberIndex],
                                str[genderIndex],
                                str[birthdayIndex]
                            )
                        }
                    }
                }
                linePosition++
            }
        } catch (e: Exception) {
            Log.e("CsvToSqlError", "" + e)
        }
    }


    fun csvToRecordSql(mContext: Context, filepath: Uri) {
        var dataBase = SqlDatabase(mContext)
        try {
            var fileInputStream = mContext.contentResolver.openInputStream(filepath)
            val r = BufferedReader(InputStreamReader(fileInputStream))
            var mLine: String?
            var hospitalNameIndex = 0
            var numberIndex = 0
            var genderIndex = 0
            var birthdayIndex = 0
            var fileNameIndex = 0
            var recordDateIndex = 0
            var beforePercentIndex = 0
            var afterPercentIndex = 0
            while (r.readLine().also { mLine = it } != null) {
                var str = (mLine as String).split(",".toRegex())
                if (linePosition == 0) {
                    for (index in str.indices) {
                        println(str[index])
                        when (str[index]) {
                            hospitalName -> hospitalNameIndex = index
                            number -> numberIndex = index
                            gender -> genderIndex = index
                            birthday -> birthdayIndex = index
                            fileName -> fileNameIndex = index
                            recordDate -> recordDateIndex = index
                            beforePercent -> beforePercentIndex = index
                            afterPercent -> afterPercentIndex = index
                        }
                    }
                }
                if (linePosition > 0) {
                    var recordEntity = RecordEntity()
                    recordEntity.hospitalName = str[hospitalNameIndex]
                    recordEntity.number = str[numberIndex]
                    recordEntity.gender = str[genderIndex]
                    recordEntity.birthday = str[birthdayIndex]
                    recordEntity.fileName = str[fileNameIndex]
                    recordEntity.recordDate = str[recordDateIndex]
                    recordEntity.beforePercent = str[beforePercentIndex]
                    recordEntity.afterPercent = str[afterPercentIndex]

                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getRecordDao().importPatientRecord(
                                recordEntity.hospitalName,
                                recordEntity.number,
                                recordEntity.gender,
                                recordEntity.birthday,
                                recordEntity.fileName,
                                recordEntity.recordDate,
                                recordEntity.beforePercent,
                                recordEntity.afterPercent
                            )
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