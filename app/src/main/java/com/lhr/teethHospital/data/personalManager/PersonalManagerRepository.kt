package com.lhr.teethHospital.data.personalManager

import android.app.Application
import com.lhr.teethHospital.model.HospitalInfo
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalManagerRepository(application: Application) {

    fun fetchHospitalInfo() {
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                hospitalEntityList.clear()
                hospitalInfoList.clear()
                // 取得所有患者細節資料
                hospitalEntityList = SqlDatabase.getInstance().getHospitalDao().getAll() as ArrayList<HospitalEntity>

                // 取得醫院名稱跟患者總數列表
                hospitalInfoList = hospitalEntityList
                    .groupBy { it.hospitalName }
                    .map { (name, entities) -> HospitalInfo(name, entities.size) } as ArrayList<HospitalInfo>
            }
        }
    }

}