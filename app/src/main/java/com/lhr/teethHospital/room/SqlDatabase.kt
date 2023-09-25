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
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. 创建一个新的表，包含除 'afterPercent' 以外的所有列
                database.execSQL("CREATE TABLE new_your_entity (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "hospitalName TEXT NOT NULL," +
                        "number TEXT NOT NULL," +
                        "gender TEXT NOT NULL," +
                        "birthday TEXT NOT NULL," +
                        "fileName TEXT NOT NULL," +
                        "recordDate TEXT NOT NULL," +
                        "detectPercent TEXT NOT NULL" +
                        // 添加其他列
                        ")")

                // 2. 将旧表中的数据复制到新表中
                database.execSQL("INSERT INTO new_your_entity SELECT id,hospitalName,number,gender,birthday,fileName,recordDate,beforePercent FROM $RECORD_TABLE_NAME")

                // 3. 删除旧表
                database.execSQL("DROP TABLE $RECORD_TABLE_NAME")

                // 4. 将新表重命名为旧表的名称
                database.execSQL("ALTER TABLE new_your_entity RENAME TO $RECORD_TABLE_NAME")
            }
        }

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