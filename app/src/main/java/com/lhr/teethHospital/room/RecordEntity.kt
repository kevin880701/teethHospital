package com.lhr.teethHospital.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.android.notesk.SQLite.SqlModel
import com.android.notesk.SQLite.SqlModel.Companion.fileName
import java.io.Serializable


@Entity(tableName = SqlModel.RECORD_TABLE_NAME)
class RecordEntity : Serializable  {

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
    var birthday : String = ""

    @ColumnInfo(name = SqlModel.fileName, typeAffinity = ColumnInfo.TEXT)
    var fileName : String = ""

    @ColumnInfo(name = SqlModel.recordDate, typeAffinity = ColumnInfo.TEXT)
    var recordDate : String = ""

    @ColumnInfo(name = SqlModel.detectPercent, typeAffinity = ColumnInfo.TEXT)
    var detectPercent : String = ""
}