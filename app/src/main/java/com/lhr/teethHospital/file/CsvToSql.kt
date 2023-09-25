package com.lhr.teethHospital.file

import android.content.Context
import android.net.Uri
import android.util.Log
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
                            mContext.getString(R.string.hospital) -> hospitalNameIndex = index
                            mContext.getString(R.string.patient_number) -> numberIndex = index
                            mContext.getString(R.string.gender) -> genderIndex = index
                            mContext.getString(R.string.birthday) -> birthdayIndex = index
                            mContext.getString(R.string.file_name) -> fileNameIndex = index
                            mContext.getString(R.string.record_date) -> recordDateIndex = index
                            mContext.getString(R.string.before_percent) -> beforePercentIndex = index
                            mContext.getString(R.string.after_percent) -> afterPercentIndex = index
                        }
                    }
                }
                println("$hospitalNameIndex,$numberIndex")
                if (linePosition > 0) {
                    var recordEntity = RecordEntity()
                    recordEntity.hospitalName = str[hospitalNameIndex]
                    recordEntity.number = str[numberIndex]
                    recordEntity.gender = str[genderIndex]
                    recordEntity.birthday = str[birthdayIndex]
                    recordEntity.fileName = str[fileNameIndex]
                    recordEntity.recordDate = str[recordDateIndex]
                    recordEntity.detectPercent = str[beforePercentIndex]

                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getRecordDao().importPatientRecord(
                                recordEntity.hospitalName,
                                recordEntity.number,
                                recordEntity.gender,
                                recordEntity.birthday,
                                recordEntity.fileName,
                                recordEntity.recordDate,
                                recordEntity.detectPercent
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