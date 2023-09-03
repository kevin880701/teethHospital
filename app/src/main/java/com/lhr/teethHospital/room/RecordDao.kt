package com.lhr.teethHospital.room

import android.database.Cursor
import androidx.room.*
import com.android.notesk.SQLite.SqlModel
import com.android.notesk.SQLite.SqlModel.Companion.RECORD_TABLE_NAME

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: RecordEntity)

    @Query("SELECT * FROM " + RECORD_TABLE_NAME + " WHERE " +
            SqlModel.hospitalName + " = :hospitalName AND "+ SqlModel.number +" = :number")
    fun getPatientRecord(hospitalName: String, number: String): List<RecordEntity>

    @Query("SELECT * FROM " + RECORD_TABLE_NAME + " WHERE " + SqlModel.hospitalName + " = :hospitalName")
    fun getPatientRecordByHospitalName(hospitalName: String): List<RecordEntity>

    @Query("DELETE FROM " + RECORD_TABLE_NAME + " WHERE " + SqlModel.fileName + " = :fileName")
    fun deleteRecord(fileName: String)

    @Query("UPDATE " + RECORD_TABLE_NAME + " SET " + SqlModel.hospitalName + " = :hospitalName, " +
            SqlModel.number + " = :number, " +
            SqlModel.gender + " = :gender, " +
            SqlModel.birthday + " = :birthday" +
            " WHERE " + SqlModel.hospitalName + " = :oldHospitalName AND "+ SqlModel.number +" = :oldNumber")
    fun updateRecord(oldHospitalName: String, oldNumber: String, hospitalName: String, number: String, gender: String, birthday: String)

    @Query("SELECT * FROM " + RECORD_TABLE_NAME +
            " WHERE " + SqlModel.hospitalName + " = :oldHospitalName AND "+ SqlModel.number +" = :oldNumber")
    fun selectUpdateFileName(oldHospitalName: String, oldNumber: String): List<RecordEntity>

    @Query("UPDATE " + RECORD_TABLE_NAME + " SET " + SqlModel.fileName + " = :newFileName" +
            " WHERE " + SqlModel.fileName + " = :oldFileName")
    fun updateFileName(oldFileName: String, newFileName: String)

    @Query("SELECT * FROM ${RECORD_TABLE_NAME}")
    fun getCursor(): Cursor

    @Query("INSERT OR IGNORE INTO ${RECORD_TABLE_NAME}(${SqlModel.hospitalName}, ${SqlModel.number}, ${SqlModel.gender}," +
            " ${SqlModel.birthday}, ${SqlModel.fileName}, ${SqlModel.recordDate}, ${SqlModel.beforePercent}, ${SqlModel.afterPercent}) " +
            "VALUES (:hospitalName, :number, :gender, :birthday, :fileName, :recordDate, :beforePercent, :afterPercent)")
    fun importPatientRecord(hospitalName: String, number: String, gender: String, birthday: String,
                            fileName: String, recordDate: String, beforePercent: String, afterPercent: String)
}