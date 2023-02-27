package com.lhr.teethHospital.util

import android.view.View
import com.lhr.teethHospital.Model.FileManager
import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.RecyclerViewAdapter.PatientRecordAdapter
import com.lhr.teethHospital.Room.RecordEntity
import com.lhr.teethHospital.Room.SqlDatabase
import com.lhr.teethHospital.util.ClassmateInformation.PatientInformationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PatientInformationPresenter(classmateInformationActivity: PatientInformationActivity) {
    var classmateInformationActivity = classmateInformationActivity
    val dataBase = SqlDatabase(classmateInformationActivity)
    val fileManager = FileManager()

    fun getRecord(hospitalName: String, number: String): ArrayList<RecordEntity>{
        lateinit var patientRecordList: ArrayList<RecordEntity>
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                patientRecordList = dataBase.getRecordDao().getPatientRecord(hospitalName, number) as ArrayList<RecordEntity>
            }
        }
        return patientRecordList
    }

    fun updateRecycler(hospitalName: String, number: String){
        var list = getRecord(hospitalName, number)
        classmateInformationActivity.patientRecordAdapter = PatientRecordAdapter(classmateInformationActivity, list)
        classmateInformationActivity.recyclerCleanRecord.adapter = classmateInformationActivity.patientRecordAdapter
    }

    fun showCheckBox(){
        classmateInformationActivity.isShowCheckBox = true
        classmateInformationActivity.patientRecordAdapter.notifyDataSetChanged()
        classmateInformationActivity.imageAdd.visibility = View.INVISIBLE
        classmateInformationActivity.imageDelete.visibility = View.VISIBLE
    }

    fun deleteRecord(){
        classmateInformationActivity.patientRecordAdapter.arrayList.removeAll(classmateInformationActivity.patientRecordAdapter.deleteList.toSet())
        classmateInformationActivity.patientRecordAdapter.notifyDataSetChanged()
        classmateInformationActivity.patientRecordAdapter.deleteList.stream().forEach { recordEntity ->
            fileManager.deleteDirectory(TEETH_DIR + recordEntity.fileName + "/", recordEntity)
        }
        classmateInformationActivity.patientRecordAdapter.deleteList.clear()
        back()
    }

    fun back(){
        if(classmateInformationActivity.isShowCheckBox){
            classmateInformationActivity.isShowCheckBox = false
            classmateInformationActivity.patientRecordAdapter.notifyDataSetChanged()
            classmateInformationActivity.imageAdd.visibility = View.VISIBLE
            classmateInformationActivity.imageDelete.visibility = View.INVISIBLE
            classmateInformationActivity.patientRecordAdapter.deleteList.clear()
        }else{
            classmateInformationActivity.finish()
        }
    }

}