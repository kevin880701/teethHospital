package com.android.notesk.SQLite

class SqlModel {
    companion object {
        const val DB_NAME : String = "HospitalInfo.db"
        const val HOSPITAL_TABLE_NAME : String = "HospitalInfo"
        const val RECORD_TABLE_NAME : String = "RecordInfo"
        const val SQLITE_SEQUENCE : String = "sqlite_sequence"
        const val id : String = "id"
        const val hospitalName : String = "hospitalName"
        const val number : String = "number"
        const val gender : String = "gender"
        const val birthday : String = "birthday"
        const val fileName : String = "fileName"
        const val recordDate : String = "recordDate"
        const val detectPercent : String = "detectPercent"
    }
}