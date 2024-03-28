package com.lhr.teethHospital.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.notesk.SQLite.SqlModel
import com.android.notesk.SQLite.SqlModel.Companion.DB_NAME
import com.android.notesk.SQLite.SqlModel.Companion.RECORD_TABLE_NAME

@Database(entities = [HospitalEntity::class,RecordEntity::class], version = 2, exportSchema = false)
abstract class SqlDatabase : RoomDatabase() {


    companion object {
        private var instance: SqlDatabase?=null
        fun getInstance(): SqlDatabase {
            return instance!!
        }
        fun init(context: Context): SqlDatabase {
            return instance ?:Room.databaseBuilder(context, SqlDatabase::class.java,DB_NAME)
                .allowMainThreadQueries()
                .build().also {
                    instance = it
                }
        }
    }

    abstract fun getHospitalDao(): HospitalDao
    abstract fun getRecordDao(): RecordDao
}