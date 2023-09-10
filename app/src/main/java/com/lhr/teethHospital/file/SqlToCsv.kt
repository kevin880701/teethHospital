package com.lhr.teethHospital.file

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.android.notesk.SQLite.SqlModel.Companion.birthday
import com.android.notesk.SQLite.SqlModel.Companion.gender
import com.android.notesk.SQLite.SqlModel.Companion.hospitalName
import com.android.notesk.SQLite.SqlModel.Companion.id
import com.android.notesk.SQLite.SqlModel.Companion.number
import com.lhr.teethHospital.R
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class SqlToCsv {
    //    var dataBase : SqlDatabase
//    lateinit var cursor : Cursor
//
//    constructor(mContext : Context, cursor: Cursor, csvFilePath: String) {
//        dataBase = SqlDatabase(mContext)
//
//        var rowcount = 0
//        var colcount = 0
//        // the name of the file to export with
//        var saveFile = File(csvFilePath)
//        var fw = FileWriter(saveFile)
//
//        var bw = BufferedWriter(fw)
//        rowcount = cursor.count
//        colcount = cursor.columnCount
//        if (rowcount > 0) {
//            cursor.moveToFirst()
//            // 欄位標頭
//            for (i in 0 until colcount step 1) {
//                if (i != colcount - 1) {
//                    when (cursor.getColumnName(i)) {
//                        hospitalName -> bw.write(mContext.getString(R.string.hospital) + ",")
//                        number -> bw.write(mContext.getString(R.string.patient_number) + ",")
//                        gender -> bw.write(mContext.getString(R.string.gender) + ",")
//                        birthday -> bw.write(mContext.getString(R.string.birthday) + ",")
//                        else -> bw.write(cursor.getColumnName(i) + ",")
//                    }
////                    bw.write(cursor.getColumnName(i) + ",")
//                } else {
//                    when (cursor.getColumnName(i)) {
//                        hospitalName -> bw.write(mContext.getString(R.string.hospital))
//                        number -> bw.write(mContext.getString(R.string.patient_number))
//                        gender -> bw.write(mContext.getString(R.string.gender))
//                        birthday -> bw.write(mContext.getString(R.string.birthday))
//                        else -> bw.write(cursor.getColumnName(i))
//                    }
////                    bw.write(cursor.getColumnName(i))
//                }
//            }
//            bw.newLine()
//
//            // 欄位內容
//            for (i in 0 until rowcount step 1) {
//                cursor.moveToPosition(i)
//                for (j in 0 until colcount step 1) {
//                    if (j != colcount - 1)
//                        bw.write(cursor.getString(j) + ",")
//                    else
//                        bw.write(cursor.getString(j))
//                }
//                bw.newLine()
//            }
//            bw.flush()
//        }
//    }
//
    fun hospitalSqlToCsv(mContext: Context, cursor: Cursor, csvFilePath: String) {
        val dataBase = SqlDatabase(mContext)
        val csvFile = File(csvFilePath)
        val hospitalName = mContext.getString(R.string.hospital)
        val patientNumber = mContext.getString(R.string.patient_number)
        val gender = mContext.getString(R.string.gender)
        val birthday = mContext.getString(R.string.birthday)

        try {
            csvFile.printWriter().use { out ->
                runBlocking {     // 阻塞主執行緒
                    launch(Dispatchers.IO) {
                        var hospitalList =
                            dataBase.getHospitalDao().getAll() as ArrayList<HospitalEntity>
                        // CSV 文件的標題行
                        out.println("$hospitalName,$patientNumber,$gender,$birthday")

                        // 將數據每一行寫入 CSV 文件
                        for (hospital in hospitalList) {
                            println("$${hospital.hospitalName},${hospital.number},${hospital.gender},${hospital.birthday}")
                            out.println("${hospital.hospitalName},${hospital.number},${hospital.gender},${hospital.birthday}")
                        }
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun recordSqlToCsv(mContext: Context, cursor: Cursor, csvFilePath: String) {
        val dataBase = SqlDatabase(mContext)
        val csvFile = File(csvFilePath)
        val hospitalName = mContext.getString(R.string.hospital)
        val patientNumber = mContext.getString(R.string.patient_number)
        val gender = mContext.getString(R.string.gender)
        val birthday = mContext.getString(R.string.birthday)
        val fileName = mContext.getString(R.string.file_name)
        val recordDate = mContext.getString(R.string.record_date)
        val beforePercent = mContext.getString(R.string.before_percent)
        val afterPercent = mContext.getString(R.string.after_percent)

        try {
            csvFile.printWriter().use { out ->
                runBlocking {     // 阻塞主執行緒
                    launch(Dispatchers.IO) {
                        var recordList =
                            dataBase.getRecordDao().getAll() as ArrayList<RecordEntity>
                        // CSV 文件的標題行
                        out.println("$hospitalName,$patientNumber,$gender,$birthday,$fileName,$recordDate,$beforePercent,$afterPercent")

                        // 將數據每一行寫入 CSV 文件
                        for (record in recordList) {
                            println("${record.hospitalName},${record.number},${record.gender},${record.birthday},${record.fileName},${record.recordDate},${record.beforePercent},${record.afterPercent}")
                            out.println("${record.hospitalName},${record.number},${record.gender},${record.birthday},${record.fileName},${record.recordDate},${record.beforePercent},${record.afterPercent}")
                        }
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//    fun hospitalSqlToCsv(mContext: Context, cursor: Cursor, csvFilePath: String) {
//        var rowcount = 0
//        var colcount = 0
//        // the name of the file to export with
//        var saveFile = File(csvFilePath)
//        var fw = FileWriter(saveFile)
//
//        var bw = BufferedWriter(fw)
//        rowcount = cursor.count
//        colcount = cursor.columnCount
//        if (rowcount > 0) {
//            cursor.moveToFirst()
//            // 欄位標頭
//            for (i in 0 until colcount step 1) {
//                if (i != colcount - 1) {
//                    when (cursor.getColumnName(i)) {
//                        hospitalName -> bw.write(mContext.getString(R.string.hospital) + ",")
//                        number -> bw.write(mContext.getString(R.string.patient_number) + ",")
//                        gender -> bw.write(mContext.getString(R.string.gender) + ",")
//                        birthday -> bw.write(mContext.getString(R.string.birthday) + ",")
//                        else -> bw.write(cursor.getColumnName(i) + ",")
//                    }
////                    bw.write(cursor.getColumnName(i) + ",")
//                } else {
//                    when (cursor.getColumnName(i)) {
//                        hospitalName -> bw.write(mContext.getString(R.string.hospital))
//                        number -> bw.write(mContext.getString(R.string.patient_number))
//                        gender -> bw.write(mContext.getString(R.string.gender))
//                        birthday -> bw.write(mContext.getString(R.string.birthday))
//                        else -> bw.write(cursor.getColumnName(i))
//                    }
////                    bw.write(cursor.getColumnName(i))
//                }
//            }
//            bw.newLine()
//
//            // 欄位內容
//            for (i in 0 until rowcount step 1) {
//                cursor.moveToPosition(i)
//                for (j in 0 until colcount step 1) {
//                    if (j != colcount - 1)
//                        bw.write(cursor.getString(j) + ",")
//                    else
//                        bw.write(cursor.getString(j))
//                }
//                bw.newLine()
//            }
//            bw.flush()
//        }
//    }

//    fun recordSqlToCsv(mContext: Context, cursor: Cursor, csvFilePath: String) {
//        var dataBase = SqlDatabase(mContext)
//        var rowcount = 0
//        var colcount = 0
//        // the name of the file to export with
//        var saveFile = File(csvFilePath)
//        var fw = FileWriter(saveFile)
//
//        var bw = BufferedWriter(fw)
//        rowcount = cursor.count
//        colcount = cursor.columnCount
//        if (rowcount > 0) {
//            cursor.moveToFirst()
//            // 欄位標頭
//            for (i in 0 until colcount step 1) {
//                if (i != colcount - 1) {
//                    bw.write(cursor.getColumnName(i) + ",")
//                } else {
//                    bw.write(cursor.getColumnName(i))
//                }
//            }
//            bw.newLine()
//
//            // 欄位內容
//            for (i in 0 until rowcount step 1) {
//                cursor.moveToPosition(i)
//                for (j in 0 until colcount step 1) {
//                    if (j != colcount - 1)
//                        bw.write(cursor.getString(j) + ",")
//                    else
//                        bw.write(cursor.getString(j))
//                }
//                bw.newLine()
//            }
//            bw.flush()
//        }
//    }
}