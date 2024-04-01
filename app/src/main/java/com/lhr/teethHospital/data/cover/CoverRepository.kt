package com.lhr.teethHospital.data.cover

import android.app.Application
import com.lhr.teethHospital.data.GroupInfo
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoverRepository(application: Application) {
    var application = application

    fun fetchHospitalInfo() {
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {

                Model.hospitalEntityList.clear()
                Model.hospitalInfoList.clear()
                // 取得所有患者細節資料
                Model.hospitalEntityList = SqlDatabase.getInstance().getHospitalDao().getAll() as ArrayList<HospitalEntity>

                // 取得醫院名稱跟患者總數列表
                Model.hospitalInfoList = Model.hospitalEntityList
                    .groupBy { it.hospitalName }
                    .map { (name, entities) -> GroupInfo(name, entities.size) } as ArrayList<GroupInfo>
            }
        }
    }
}