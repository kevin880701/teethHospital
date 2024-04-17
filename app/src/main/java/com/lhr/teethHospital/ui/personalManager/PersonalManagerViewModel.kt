package com.lhr.teethHospital.ui.personalManager

import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.util.recyclerViewAdapter.PatientAdapter
import com.lhr.teethHospital.util.recyclerViewAdapter.PersonalManagerAdapter
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.databinding.FragmentPersonalManagerBinding
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseViewModel
import com.lhr.teethHospital.ui.login.LoginActivity
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalManagerViewModel(context: Context, var personalManagerRepository: PersonalManagerRepository) :
    BaseViewModel(context) {
    companion object {
        const val CLASS_LIST: Int = 100
        const val CLASS_INFO_LIST: Int = 101
        var recyclerInfoStatus: MutableLiveData<Int> =
            MutableLiveData<Int>().apply { value = CLASS_LIST }
        var isShowCheckBox: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
        var titleBarText: MutableLiveData<String> =
            MutableLiveData<String>().apply { value = "" }
    }

    val fileManager = FileManager()

    fun deleteRecord(
        binding: FragmentPersonalManagerBinding,
        personalManagerFragment: PersonalManagerFragment
    ) {
        when (recyclerInfoStatus.value) {
            CLASS_LIST -> {
                var adapter = binding.recyclerInfo.adapter as PersonalManagerAdapter
                hospitalInfoList.removeAll(adapter.deleteList.toSet())
                adapter.notifyDataSetChanged()
                adapter.deleteList.stream().forEach { classInfo ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            SqlDatabase.getInstance().getHospitalDao()
                                .deleteRecordByClassName(classInfo.groupName)
                            var patientRecordList = SqlDatabase.getInstance().getRecordDao()
                                .getPatientRecordByHospitalName(classInfo.groupName) as ArrayList<RecordEntity>
                            patientRecordList.stream().forEach { recordEntity ->
                                fileManager.deleteDirectory(
                                    TEETH_DIR + recordEntity.fileName + "/",
                                    recordEntity,
                                    personalManagerFragment.requireActivity()
                                )
                            }
                        }
                    }
                }
                adapter.deleteList.clear()
            }

            CLASS_INFO_LIST -> {
                var adapter = binding.recyclerInfo.adapter as PatientAdapter
                adapter.arrayList.removeAll(adapter.deleteList.toSet())
                adapter.notifyDataSetChanged()

                adapter.deleteList.stream().forEach { hospitalEntity ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            SqlDatabase.getInstance().getHospitalDao()
                                .deleteRecord(hospitalEntity.hospitalName, hospitalEntity.number)
                            var patientRecordList =
                                SqlDatabase.getInstance().getRecordDao().getPatientRecord(
                                    hospitalEntity.hospitalName,
                                    hospitalEntity.number
                                ) as ArrayList<RecordEntity>
                            patientRecordList.stream().forEach { recordEntity ->
                                fileManager.deleteDirectory(
                                    TEETH_DIR + recordEntity.fileName + "/",
                                    recordEntity,
                                    personalManagerFragment.requireActivity()
                                )
                            }
                        }
                    }
                }
                adapter.deleteList.clear()
            }
        }
        updateRecyclerInfo()
    }

    fun updateRecyclerInfo(
    ) {
        personalManagerRepository.getAllInfo()
        isProgressBar.value = false
    }

    fun back(
        binding: FragmentPersonalManagerBinding,
        personalManagerFragment: PersonalManagerFragment
    ) {
        if (isShowCheckBox.value!!) {  // 如果處於償案顯示刪除框的狀態
            isShowCheckBox.value = false
            when (recyclerInfoStatus.value) {
                CLASS_LIST -> {
                    (binding.recyclerInfo.adapter as PersonalManagerAdapter).notifyDataSetChanged()
                    (binding.recyclerInfo.adapter as PersonalManagerAdapter).deleteList.clear()
                }

                CLASS_INFO_LIST -> {
                    (binding.recyclerInfo.adapter as PatientAdapter).notifyDataSetChanged()
                    (binding.recyclerInfo.adapter as PatientAdapter).deleteList.clear()
                }
            }
        } else {
            when (recyclerInfoStatus.value) {
                CLASS_LIST -> {
//                    personalManagerFragment.requireActivity().finish()

                    val intent =
                        Intent(personalManagerFragment.requireActivity(), LoginActivity::class.java)
                    personalManagerFragment.requireActivity().startActivity(intent)
                    personalManagerFragment.requireActivity().finish()
                }

                CLASS_INFO_LIST -> {
                    binding.recyclerInfo.adapter = PersonalManagerAdapter(personalManagerFragment)
//                    binding.textTitleBar.text = personalManagerFragment.requireActivity()
//                        .getString(R.string.hospital_information)
                    recyclerInfoStatus.value = CLASS_LIST
                }
            }
        }
    }
}