package com.lhr.teethHospital.model

import com.lhr.teethHospital.data.GroupInfo
import com.lhr.teethHospital.room.entity.HospitalEntity
import java.io.File

class Model {
    companion object {
        var CleanRecordFilter = "cleanRecord"
        var UPDATE_PATIENT_RECORD = "UPDATE_PATIENT_RECORD"
        const val IMAGE_REQUEST_CODE : Int = 100
        const val IMPORT_CSV : Int = 300
        const val IMAGE_URI : String = "IMAGE_URI"
        val RESULT_FILE_NAME = "result.png"
        val originalPictureFileName = "originalPicture.png"
        val rangePictureFileName = "rangePicture.png"
        val detectPictureFileName = "detectPicture.png"
        val CAMERA_INTENT_FILTER = "CAMERA_INTENT_FILTER"
        val RECORD_DATE = "RECORD_DATE"
        val ORIGINAL_PICTURE = "ORIGINAL_PICTURE"
        val DETECT_PERCENT = "DETECT_PERCENT"
        val DETECT_PICTURE = "DETECT_PICTURE"
        val PERCENT_RECORD = "percent.txt"
        var APP_FILES_PATH : String = "" // /storage/emulated/0/Android/data/com.lhr.teethHospital/files
        var DATABASES_PATH : String = "" // /data/user/0/com.lhr.teethHospital/databases
        var TEETH_DIR = "" // /storage/emulated/0/Android/data/com.lhr.teethHospital/files/teeth/
        val BACKUP_NAME : String = "teethHospitalBackup.rar"
        val HOSPITAL_CSV = "hospitalBackUp.csv"
        val RECORD_CSV = "recordBackUp.csv"
        val ROOT = "ROOT"
        val PATIENT = "PATIENT"
        lateinit var allFileList: Array<File>
        var hospitalInfoList : ArrayList<GroupInfo> = ArrayList()
        var hospitalEntityList : ArrayList<HospitalEntity> = ArrayList()
    }
}