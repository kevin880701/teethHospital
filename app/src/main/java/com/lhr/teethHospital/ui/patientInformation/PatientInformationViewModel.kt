package com.lhr.teethHospital.ui.patientInformation

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.recyclerViewAdapter.PatientRecordAdapter
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.data.patientInformation.PatientInformationRepository

class PatientInformationViewModel(application: Application) : AndroidViewModel(application) {

    var isShowCheckBox: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    var patientInformationRepository = PatientInformationRepository(application)
    val fileManager = FileManager()

    fun getRecord(hospitalName: String, number: String): ArrayList<RecordEntity>{
        return patientInformationRepository.fetchPatientRecord(hospitalName, number)
    }

    fun deleteRecord(patientInformationActivity: PatientInformationActivity){
        patientInformationActivity.patientRecordAdapter.arrayList.removeAll(patientInformationActivity.patientRecordAdapter.deleteList.toSet())
        patientInformationActivity.patientRecordAdapter.notifyDataSetChanged()
        patientInformationActivity.patientRecordAdapter.deleteList.stream().forEach { recordEntity ->
            FileManager().deleteDirectory(Model.TEETH_DIR + recordEntity.fileName + "/", recordEntity, patientInformationActivity)
        }
        patientInformationActivity.patientRecordAdapter.deleteList.clear()

        val intent = Intent("updateRecyclerInfo")
        patientInformationActivity.sendBroadcast(intent)
        back(patientInformationActivity)
    }

    fun updateRecycler(patientRecordAdapter: PatientRecordAdapter, hospitalName: String, number: String){
        patientRecordAdapter.arrayList = getRecord(hospitalName, number)
        patientRecordAdapter.notifyDataSetChanged()
    }

    fun back(patientInformationActivity: PatientInformationActivity){
        if(isShowCheckBox.value!!){
            isShowCheckBox.value = false
            patientInformationActivity.patientRecordAdapter.notifyDataSetChanged()
            patientInformationActivity.binding.imageAdd.visibility = View.VISIBLE
            patientInformationActivity.binding.imageDelete.visibility = View.INVISIBLE
            patientInformationActivity.patientRecordAdapter.deleteList.clear()
        }else{
            patientInformationActivity.finish()
        }
    }
}