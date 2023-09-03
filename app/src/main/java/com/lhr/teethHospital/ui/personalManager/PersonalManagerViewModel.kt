package com.lhr.teethHospital.ui.personalManager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.R
import com.lhr.teethHospital.recyclerViewAdapter.PatientAdapter
import com.lhr.teethHospital.recyclerViewAdapter.PersonalManagerAdapter
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.data.personalManager.PersonalManagerRepository
import com.lhr.teethHospital.databinding.FragmentPersonalManagerBinding
import com.lhr.teethHospital.ui.main.MainViewModel
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors

class PersonalManagerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val CLASS_LIST: Int = 100
        const val CLASS_INFO_LIST: Int = 101
        var recyclerInfoStatus: MutableLiveData<Int> =
            MutableLiveData<Int>().apply { value = CLASS_LIST }
        var isShowCheckBox: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
        var isPersonalManagerBack: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
    }

    var personalManagerRepository = PersonalManagerRepository(application)
    val fileManager = FileManager()

    fun getHospitalInfo() {
        personalManagerRepository.fetchHospitalInfo()
    }

    fun deleteRecord(binding: FragmentPersonalManagerBinding,
                     personalManagerFragment: PersonalManagerFragment) {
        when (recyclerInfoStatus.value) {
            CLASS_LIST -> {
                var adapter = binding.recyclerInfo.adapter as PersonalManagerAdapter
                hospitalInfoList.removeAll(adapter.deleteList.toSet())
                adapter.notifyDataSetChanged()
                adapter.deleteList.stream().forEach { classInfo ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            personalManagerFragment.dataBase.getHospitalDao().deleteRecordByClassName(classInfo.className)
                            var patientRecordList = personalManagerFragment.dataBase.getRecordDao()
                                .getPatientRecordByHospitalName(classInfo.className) as ArrayList<RecordEntity>
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

                adapter.deleteList.stream().forEach { classEntity ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            personalManagerFragment.dataBase.getHospitalDao()
                                .deleteRecord(classEntity.hospitalName, classEntity.number)
                            var patientRecordList = personalManagerFragment.dataBase.getRecordDao().getPatientRecord(
                                classEntity.hospitalName,
                                classEntity.number
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
        updateRecyclerInfo(binding, personalManagerFragment)
        isPersonalManagerBack.value = true
    }

    fun updateRecyclerInfo(binding: FragmentPersonalManagerBinding, personalManagerFragment: PersonalManagerFragment) {
        if (binding.recyclerInfo.adapter is PersonalManagerAdapter) {
            (binding.recyclerInfo.adapter as PersonalManagerAdapter).clearItems()
            getHospitalInfo()
            binding.recyclerInfo.adapter = PersonalManagerAdapter(personalManagerFragment)
        } else if (binding.recyclerInfo.adapter is PatientAdapter) {
            (binding.recyclerInfo.adapter as PatientAdapter).clearItems()
            getHospitalInfo()
            var list = hospitalEntityList.stream()
                .filter { classEntity -> classEntity.hospitalName == binding.textTitleBar.text }.collect(
                    Collectors.toList()
                ) as java.util.ArrayList<HospitalEntity>
            var classmateAdapter = PatientAdapter(list, personalManagerFragment)
            binding.recyclerInfo.adapter = classmateAdapter
        }
        isProgressBar.value = false
    }

    fun back(
        binding: FragmentPersonalManagerBinding,
        personalManagerFragment: PersonalManagerFragment
    ) {
        if (isShowCheckBox.value!!) {
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
                    personalManagerFragment.requireActivity().finish()
                }

                CLASS_INFO_LIST -> {
                    binding.recyclerInfo.adapter = PersonalManagerAdapter(personalManagerFragment)
                    binding.textTitleBar.text = personalManagerFragment.requireActivity()
                        .getString(R.string.hospital_information)
                    recyclerInfoStatus.value = CLASS_LIST
                }
            }
        }
    }
}