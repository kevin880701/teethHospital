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
import com.lhr.teethHospital.room.SqlDatabase
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

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
    fun hospitalSqlToCsv(mContext : Context, cursor: Cursor, csvFilePath: String){
        var dataBase = SqlDatabase(mContext)
        var rowcount = 0
        var colcount = 0
        // the name of the file to export with
        var saveFile = File(csvFilePath)
        var fw = FileWriter(saveFile)

        var bw = BufferedWriter(fw)
        rowcount = cursor.count
        colcount = cursor.columnCount
        if (rowcount > 0) {
            cursor.moveToFirst()
            // 欄位標頭
            for (i in 0 until colcount step 1) {
                if (i != colcount - 1) {
                    when (cursor.getColumnName(i)) {
                        hospitalName -> bw.write(mContext.getString(R.string.hospital) + ",")
                        number -> bw.write(mContext.getString(R.string.patient_number) + ",")
                        gender -> bw.write(mContext.getString(R.string.gender) + ",")
                        birthday -> bw.write(mContext.getString(R.string.birthday) + ",")
                        else -> bw.write(cursor.getColumnName(i) + ",")
                    }
//                    bw.write(cursor.getColumnName(i) + ",")
                } else {
                    when (cursor.getColumnName(i)) {
                        hospitalName -> bw.write(mContext.getString(R.string.hospital))
                        number -> bw.write(mContext.getString(R.string.patient_number))
                        gender -> bw.write(mContext.getString(R.string.gender))
                        birthday -> bw.write(mContext.getString(R.string.birthday))
                        else -> bw.write(cursor.getColumnName(i))
                    }
//                    bw.write(cursor.getColumnName(i))
                }
            }
            bw.newLine()

            // 欄位內容
            for (i in 0 until rowcount step 1) {
                cursor.moveToPosition(i)
                for (j in 0 until colcount step 1) {
                    if (j != colcount - 1)
                        bw.write(cursor.getString(j) + ",")
                    else
                        bw.write(cursor.getString(j))
                }
                bw.newLine()
            }
            bw.flush()
        }
    }

    fun recordSqlToCsv(mContext : Context, cursor: Cursor, csvFilePath: String){
        var dataBase = SqlDatabase(mContext)
        var rowcount = 0
        var colcount = 0
        // the name of the file to export with
        var saveFile = File(csvFilePath)
        var fw = FileWriter(saveFile)

        var bw = BufferedWriter(fw)
        rowcount = cursor.count
        colcount = cursor.columnCount
        if (rowcount > 0) {
            cursor.moveToFirst()
            // 欄位標頭
            for (i in 0 until colcount step 1) {
                if (i != colcount - 1) {
                    bw.write(cursor.getColumnName(i) + ",")
                } else {
                    bw.write(cursor.getColumnName(i))
                }
            }
            bw.newLine()

            // 欄位內容
            for (i in 0 until rowcount step 1) {
                cursor.moveToPosition(i)
                for (j in 0 until colcount step 1) {
                    if (j != colcount - 1)
                        bw.write(cursor.getString(j) + ",")
                    else
                        bw.write(cursor.getString(j))
                }
                bw.newLine()
            }
            bw.flush()
        }
    }
}