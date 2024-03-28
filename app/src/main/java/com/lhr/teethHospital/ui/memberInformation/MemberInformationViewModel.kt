package com.lhr.teethHospital.ui.memberInformation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.util.recyclerViewAdapter.PatientRecordAdapter
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.ui.base.APP

    class MemberInformationViewModel(context: Context,var personalManagerRepository: PersonalManagerRepository) :
        AndroidViewModel(context.applicationContext as APP) {
    var isShowCheckBox: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }

    fun getMemberRecord(hospitalName: String, number: String): ArrayList<RecordEntity>{
        return personalManagerRepository.getMemberRecord(hospitalName, number)
    }

    fun deleteRecord(patientInformationActivity: MemberInformationActivity){
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
        patientRecordAdapter.arrayList = getMemberRecord(hospitalName, number)
        patientRecordAdapter.notifyDataSetChanged()
    }

    fun back(patientInformationActivity: MemberInformationActivity){
        if(isShowCheckBox.value!!){
            isShowCheckBox.value = false
            patientInformationActivity.patientRecordAdapter.notifyDataSetChanged()
//            patientInformationActivity.binding.imageAdd.visibility = View.VISIBLE
//            patientInformationActivity.binding.imageDelete.visibility = View.INVISIBLE
            patientInformationActivity.patientRecordAdapter.deleteList.clear()
        }else{
            patientInformationActivity.finish()
        }
    }
}