package com.lhr.teethHospital.Model

import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.util.Main.MainActivity
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment
import com.lhr.teethHospital.util.Setting.SettingFragment
import java.io.File

class Model {
    companion object {
        var mainActivity: MainActivity = MainActivity()
        var CleanRecordFilter = "cleanRecord"
        var UPDATE_CLEAN_RECORD = "UPDATE_CLEAN_RECORD"
        var UPDATE_RECYCLERVIEW = "UPDATE_RECYCLERVIEW"

        const val IMAGE_REQUEST_CODE : Int = 100
        const val IMPORT_CSV : Int = 300
        const val IMAGE_URI : String = "IMAGE_URI"
        val PIC_FILE_NAME = "pic.png"
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
        lateinit var allFileList: Array<File>
        var hospitalInfoList : ArrayList<HospitalInfo> = ArrayList()
        var hospitalEntityList : ArrayList<HospitalEntity> = ArrayList()
        val hospitalEntityMap : HashMap<String,ArrayList<HospitalEntity>> = HashMap()

        lateinit var personalManagerFragment: PersonalManagerFragment
        lateinit var settingFragment: SettingFragment
    }
}