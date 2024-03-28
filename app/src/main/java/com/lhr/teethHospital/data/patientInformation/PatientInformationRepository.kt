package com.lhr.teethHospital.data.patientInformation

import android.app.Application
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PatientInformationRepository(application: Application) {


    fun fetchPatientRecord(hospitalName: String, number: String): ArrayList<RecordEntity>{
                lateinit var patientRecordList: ArrayList<RecordEntity>
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                patientRecordList = SqlDatabase.getInstance().getRecordDao().getPatientRecord(hospitalName, number) as ArrayList<RecordEntity>
            }
        }
        return patientRecordList
    }
}