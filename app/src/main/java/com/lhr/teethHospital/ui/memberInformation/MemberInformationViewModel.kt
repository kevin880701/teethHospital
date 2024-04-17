package com.lhr.teethHospital.ui.memberInformation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.ui.base.BaseViewModel

class MemberInformationViewModel(context: Context, var personalManagerRepository: PersonalManagerRepository) : BaseViewModel(context)  {
    var isShowCheckBox: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }

    fun getMemberRecord(hospitalName: String, number: String): ArrayList<RecordEntity> {
        return personalManagerRepository.getMemberRecord(hospitalName, number)
    }

//    fun deleteRecord(patientInformationActivity: MemberInformationActivity) {
//        patientInformationActivity.memberRecordAdapter.arrayList.removeAll(patientInformationActivity.memberRecordAdapter.deleteList.toSet())
//        patientInformationActivity.memberRecordAdapter.notifyDataSetChanged()
//        patientInformationActivity.memberRecordAdapter.deleteList.stream().forEach { recordEntity ->
//            FileManager().deleteDirectory(
//                Model.TEETH_DIR + recordEntity.fileName + "/",
//                recordEntity,
//                patientInformationActivity
//            )
//        }
//        patientInformationActivity.memberRecordAdapter.deleteList.clear()
//
//        val intent = Intent("updateRecyclerInfo")
//        patientInformationActivity.sendBroadcast(intent)
//        back(patientInformationActivity)
//    }

//    fun updateRecycler(patientRecordAdapter: MemberRecordAdapter, hospitalName: String, number: String) {
//        patientRecordAdapter.arrayList = getMemberRecord(hospitalName, number)
//        patientRecordAdapter.notifyDataSetChanged()
//    }

    fun back(patientInformationActivity: MemberInformationActivity) {
        if (isShowCheckBox.value!!) {
            isShowCheckBox.value = false
            patientInformationActivity.memberRecordAdapter.notifyDataSetChanged()
//            patientInformationActivity.binding.imageAdd.visibility = View.VISIBLE
//            patientInformationActivity.binding.imageDelete.visibility = View.INVISIBLE
//            patientInformationActivity.memberRecordAdapter.deleteList.clear()
        } else {
            patientInformationActivity.finish()
        }
    }
}