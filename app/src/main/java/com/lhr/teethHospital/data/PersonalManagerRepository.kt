package com.lhr.teethHospital.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalManagerRepository(context: Context) {

    companion object {
        private var instance: PersonalManagerRepository? = null
        fun getInstance(context: Context): PersonalManagerRepository {
            if (instance == null) {
                instance = PersonalManagerRepository(context)
            }
            return instance!!
        }
    }

    var hospitalInfoList: MutableLiveData<ArrayList<HospitalEntity>> =
        MutableLiveData<ArrayList<HospitalEntity>>().apply { value = ArrayList() }
    var groupInfoList: MutableLiveData<ArrayList<GroupInfo>> =
        MutableLiveData<ArrayList<GroupInfo>>().apply { value = ArrayList() }


    fun getMemberRecord(hospitalName: String, number: String): ArrayList<RecordEntity>{
        lateinit var patientRecordList: ArrayList<RecordEntity>
        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                patientRecordList = SqlDatabase.getInstance().getRecordDao().getPatientRecord(hospitalName, number) as ArrayList<RecordEntity>
            }
        }
        return patientRecordList
    }

    fun getAllInfo() {
        // 取得所有患者細節資料
        var hospitalEntityList = SqlDatabase.getInstance().getHospitalDao().getAll() as ArrayList<HospitalEntity>

//        // 取得醫院名稱跟患者總數列表
        var tempGroupInfoList = hospitalEntityList
            .groupBy { it.hospitalName }
            .map { (name, entities) -> GroupInfo(name, entities.size) } as ArrayList<GroupInfo>
        hospitalInfoList.postValue(hospitalEntityList)
        groupInfoList.postValue(tempGroupInfoList)
    }

}