package com.lhr.teethHospital.model

import com.lhr.teethHospital.room.HospitalEntity
import java.io.File

class Model {
    companion object {
        var CleanRecordFilter = "cleanRecord"
        var UPDATE_PATIENT_RECORD = "UPDATE_PATIENT_RECORD"
        const val IMAGE_REQUEST_CODE : Int = 100
        const val IMPORT_CSV : Int = 300
        const val IMAGE_URI : String = "IMAGE_URI"
        val RESULT_FILE_NAME = "result.png"
        val CLEAN_BEFORE_ORIGINAL = "clean_before_original.png"
        val CLEAN_BEFORE_DETECT = "clean_before_detect.png"
        val CLEAN_AFTER_ORIGINAL = "clean_after_original.png"
        val CLEAN_AFTER_DETECT = "clean_after_detect.png"
        val PERCENT_RECORD = "percent.txt"
        var CLEAN_BEFORE_EXIST = false
        var CLEAN_AFTER_EXIST = false
        var APP_FILES_PATH : String = "" // /storage/emulated/0/Android/data/com.lhr.teethHospital/files
        var DATABASES_PATH : String = "" // /data/user/0/com.lhr.teethHospital/databases
        var TEETH_DIR = "" // /storage/emulated/0/Android/data/com.lhr.teethHospital/files/teeth/
        val BACKUP_NAME : String = "teethHospitalBackup.rar"
        val HOSPITAL_CSV = "hospitalBackUp.csv"
        val RECORD_CSV = "recordBackUp.csv"
        val PROGRESSBAR = "ProgressBar"
        lateinit var allFileList: Array<File>
        var hospitalInfoList : ArrayList<HospitalInfo> = ArrayList()
        var hospitalEntityList : ArrayList<HospitalEntity> = ArrayList()
        val hospitalEntityMap : HashMap<String,ArrayList<HospitalEntity>> = HashMap()

    }
}