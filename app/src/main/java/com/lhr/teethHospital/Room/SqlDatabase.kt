package com.lhr.teethHospital.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.notesk.SQLite.SqlModel

@Database(entities = [HospitalEntity::class,RecordEntity::class], version = 1, exportSchema = false)
abstract class SqlDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = SqlModel.DB_NAME
        @Volatile private var instance: SqlDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            SqlDatabase::class.java, SqlModel.DB_NAME).build()
    }

    abstract fun getHospitalDao(): HospitalDao
    abstract fun getRecordDao(): RecordDao
}