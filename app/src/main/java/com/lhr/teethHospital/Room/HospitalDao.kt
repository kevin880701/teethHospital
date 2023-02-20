package com.lhr.teethHospital.Room

import androidx.room.*
import com.android.notesk.SQLite.SqlModel
import com.android.notesk.SQLite.SqlModel.Companion.HOSPITAL_TABLE_NAME

@Dao
interface HospitalDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun importCsv(item: HospitalEntity)

    @Query("INSERT OR IGNORE INTO $HOSPITAL_TABLE_NAME(hospitalName, number, gender, birthday)" +
            " VALUES (:hospitalName, :number, :gender, :birthday)")
    fun importPatientInformation(hospitalName: String, number: String, gender: String, birthday: String)

    @Query("SELECT * FROM " + HOSPITAL_TABLE_NAME)
    fun getAll(): List<HospitalEntity>

    @Query("DELETE FROM " + HOSPITAL_TABLE_NAME + " WHERE " + SqlModel.hospitalName + " = :hospitalName")
    fun deleteRecordByClassName(hospitalName: String)

//    @Query("DELETE FROM " + HOSPITAL_TABLE_NAME + " WHERE " + SqlModel.number + " = :number")
//    fun deleteRecord(number: String)

    @Query("DELETE FROM " + HOSPITAL_TABLE_NAME + " WHERE " + SqlModel.hospitalName + " = :hospitalName AND "+ SqlModel.number +" = :number")
    fun deleteRecord(hospitalName: String, number: String)

    @Query("UPDATE " + HOSPITAL_TABLE_NAME + " SET " +
            SqlModel.hospitalName + " = :hospitalName, " +
            SqlModel.number + " = :number, " +
            SqlModel.gender + " = :gender, " +
            SqlModel.birthday + " = :birthday" +
            " WHERE " + SqlModel.hospitalName + " = :oldHospitalName AND "+ SqlModel.number +" = :oldNumber")
    fun updatePatientInformation(oldHospitalName: String, oldNumber: String, hospitalName: String, number: String, gender: String, birthday: String)

//    @Query("UPDATE " + TABLE_NAME + " SET " + recyclerPosition + " = :newId WHERE " + recyclerPosition + " = :oldId")
//    fun delUpdateId(oldId: Int, newId: Int)
//
//    @Delete
//    fun delete(item: ClassEntity)
}