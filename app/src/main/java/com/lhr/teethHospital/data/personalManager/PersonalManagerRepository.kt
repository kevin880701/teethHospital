package com.lhr.teethHospital.data.personalManager

import android.app.Application
import com.lhr.teethHospital.model.HospitalInfo
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityMap
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalManagerRepository(application: Application) {
    val dataBase = SqlDatabase(application)

    fun fetchHospitalInfo() {
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
                    var hospitalInfo = HospitalInfo()
                    hospitalInfo.className = key
                    hospitalInfo.number = value.size
                    hospitalInfoList.add(hospitalInfo)
                }
            }
        }
    }

}