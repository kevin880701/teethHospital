package com.lhr.teethHospital.room.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.android.notesk.SQLite.SqlModel
import com.android.notesk.SQLite.SqlModel.Companion.number
import java.io.Serializable

@Entity(tableName = SqlModel.HOSPITAL_TABLE_NAME, indices = [Index(value = [number], unique = true)])
class HospitalEntity : Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = SqlModel.hospitalName, typeAffinity = ColumnInfo.TEXT)
    var hospitalName = ""

    @ColumnInfo(name = SqlModel.number, typeAffinity = ColumnInfo.TEXT)
    var number = ""

    @ColumnInfo(name = SqlModel.gender, typeAffinity = ColumnInfo.TEXT)
    var gender = ""

    @ColumnInfo(name = SqlModel.birthday, typeAffinity = ColumnInfo.TEXT)
    var birthday: String = ""

}