package com.lhr.teethHospital.File

import android.content.Context
import android.database.Cursor
import com.lhr.teethHospital.Room.SqlDatabase
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class SqlToCsv {
    var dataBase : SqlDatabase
    lateinit var cursor : Cursor

    constructor(mContext : Context, cursor: Cursor, csvFilePath: String) {
        dataBase = SqlDatabase(mContext)

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
            for (i in 0 until colcount step 1) {
                if (i != colcount - 1) {
                    bw.write(cursor.getColumnName(i) + ",")
                } else {
                    bw.write(cursor.getColumnName(i))
                }
            }
            bw.newLine()

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