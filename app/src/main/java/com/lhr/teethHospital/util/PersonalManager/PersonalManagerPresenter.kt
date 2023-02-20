/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.util.PersonalManager

import android.view.View
import com.lhr.teethHospital.Model.HospitalInfo
import com.lhr.teethHospital.Model.FileManager
import com.lhr.teethHospital.Model.Model.Companion.PICTURE_DIR
import com.lhr.teethHospital.Model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.Model.Model.Companion.hospitalEntityMap
import com.lhr.teethHospital.Model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.R
import com.lhr.teethHospital.RecyclerViewAdapter.HospitalInfoAdapter
import com.lhr.teethHospital.RecyclerViewAdapter.PatientAdapter
import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.Room.RecordEntity
import com.lhr.teethHospital.Room.SqlDatabase
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment.Companion.CLASSMATE_LIST
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment.Companion.CLASS_LIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors

class PersonalManagerPresenter(personalManagerFragment: PersonalManagerFragment) {
    var personalManagerFragment = personalManagerFragment
    val dataBase = SqlDatabase(mainActivity)
    val fileManager = FileManager()

    fun back() {
        if (personalManagerFragment.isShowCheckBox) {
            personalManagerFragment.isShowCheckBox = false
            personalManagerFragment.imageAdd.visibility = View.VISIBLE
            personalManagerFragment.imageDelete.visibility = View.INVISIBLE
            when (PersonalManagerFragment.recyclerInfoStatus) {
                CLASS_LIST -> {
                    (personalManagerFragment.recyclerInfo.adapter as HospitalInfoAdapter).notifyDataSetChanged()
                    (personalManagerFragment.recyclerInfo.adapter as HospitalInfoAdapter).deleteList.clear()
                }
                CLASSMATE_LIST -> {
                    (personalManagerFragment.recyclerInfo.adapter as PatientAdapter).notifyDataSetChanged()
                    (personalManagerFragment.recyclerInfo.adapter as PatientAdapter).deleteList.clear()
                }
            }
        } else {
            when (PersonalManagerFragment.recyclerInfoStatus) {
                CLASS_LIST -> {
                    mainActivity.finish()
                }
                CLASSMATE_LIST -> {
                    personalManagerFragment.recyclerInfo.adapter = HospitalInfoAdapter(personalManagerFragment)
                    personalManagerFragment.textClassInfo.text = mainActivity.getString(R.string.hospital_information)
                    personalManagerFragment.constrainClassmate.visibility = View.INVISIBLE
                    personalManagerFragment.textClassInfo.visibility = View.VISIBLE
                    PersonalManagerFragment.recyclerInfoStatus = CLASS_LIST
                }
            }
        }
    }

    fun getClassInformation() {
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                hospitalEntityList.clear()
                hospitalEntityMap.clear()
                hospitalEntityList = dataBase.getHospitalDao().getAll() as ArrayList<HospitalEntity>
                hospitalEntityList.stream().forEach { classEntity ->
                    if (!hospitalEntityMap.containsKey(classEntity.hospitalName)) {
                        hospitalEntityMap[classEntity.hospitalName] = ArrayList()
                        hospitalEntityMap[classEntity.hospitalName]?.add(classEntity)
                    } else {
                        hospitalEntityMap[classEntity.hospitalName]?.add(classEntity)
                    }
                }
                hospitalEntityMap.forEach { (key, value) ->
                    var classInfo = HospitalInfo()
                    classInfo.className = key
                    classInfo.number = value.size
                    hospitalInfoList.add(classInfo)
                }
            }
        }
    }

    fun updateRecyclerInfo() {
        if (personalManagerFragment.recyclerInfo.adapter is HospitalInfoAdapter) {
            (personalManagerFragment.recyclerInfo.adapter as HospitalInfoAdapter).clearItems()
            getClassInformation()
            personalManagerFragment.recyclerInfo.adapter = HospitalInfoAdapter(personalManagerFragment)
        } else if (personalManagerFragment.recyclerInfo.adapter is PatientAdapter) {
            (personalManagerFragment.recyclerInfo.adapter as PatientAdapter).clearItems()
            getClassInformation()
            var list = hospitalEntityList.stream()
                .filter { classEntity -> classEntity.hospitalName == personalManagerFragment.textClassName.text }.collect(
                    Collectors.toList()
                ) as java.util.ArrayList<HospitalEntity>
            var classmateAdapter = PatientAdapter(list, personalManagerFragment)
            personalManagerFragment.recyclerInfo.adapter = classmateAdapter
        }
    }

    fun showCheckBox() {
        personalManagerFragment.isShowCheckBox = true
        personalManagerFragment.recyclerInfo.adapter?.notifyDataSetChanged()
        personalManagerFragment.imageAdd.visibility = View.INVISIBLE
        personalManagerFragment.imageDelete.visibility = View.VISIBLE
    }

    fun deleteRecord() {
        when (PersonalManagerFragment.recyclerInfoStatus) {
            CLASS_LIST -> {
                var adapter = personalManagerFragment.recyclerInfo.adapter as HospitalInfoAdapter
                hospitalInfoList.removeAll(adapter.deleteList.toSet())
                adapter.notifyDataSetChanged()
                adapter.deleteList.stream().forEach{ classInfo ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getHospitalDao().deleteRecordByClassName(classInfo.className)
                            var patientRecordList = dataBase.getRecordDao().getPatientRecordByHospitalName(classInfo.className) as ArrayList<RecordEntity>
                            patientRecordList.stream().forEach{ recordEntity ->
                                fileManager.deleteDirectory(PICTURE_DIR + recordEntity.fileName + "/", recordEntity)
                            }
                        }
                    }
                }
                adapter.deleteList.clear()
            }
            CLASSMATE_LIST -> {
                var adapter = personalManagerFragment.recyclerInfo.adapter as PatientAdapter
                adapter.arrayList.removeAll(adapter.deleteList.toSet())
                adapter.notifyDataSetChanged()

                adapter.deleteList.stream().forEach{ classEntity ->
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getHospitalDao().deleteRecord(classEntity.hospitalName, classEntity.number)
                            var patientRecordList = dataBase.getRecordDao().getPatientRecord(classEntity.hospitalName, classEntity.number) as ArrayList<RecordEntity>
                            patientRecordList.stream().forEach{ recordEntity ->
                                    fileManager.deleteDirectory(PICTURE_DIR + recordEntity.fileName + "/", recordEntity)
                            }
                        }
                    }
                }
                adapter.deleteList.clear()
            }
        }
        updateRecyclerInfo()
        back()
    }
}