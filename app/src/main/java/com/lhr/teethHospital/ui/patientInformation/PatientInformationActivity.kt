package com.lhr.teethHospital.ui.patientInformation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lhr.teethHospital.R
import com.lhr.teethHospital.util.recyclerViewAdapter.PatientRecordAdapter
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.databinding.ActivityPatientInformationBinding
import com.lhr.teethHospital.model.Model.Companion.PATIENT
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.ui.camera.CameraActivity
import com.lhr.teethHospital.ui.editPatientInformation.EditPatientInformationActivity

class PatientInformationActivity : AppCompatActivity(), View.OnClickListener {
    companion object{
        var UPDATE_PATIENT_RECORD = "UPDATE_PATIENT_RECORD"
    }
    lateinit var viewModel: PatientInformationViewModel
    lateinit var binding: ActivityPatientInformationBinding
    lateinit var patientRecordAdapter: PatientRecordAdapter
    lateinit var hospitalEntity: HospitalEntity
    lateinit var patientRecordList: ArrayList<RecordEntity>
    lateinit var messageReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_information)
        viewModel = ViewModelProvider(
            this,
            PatientInformationViewModelFactory(this.application)
        )[PatientInformationViewModel::class.java]
        binding.viewModel = viewModel


        if (intent.getSerializableExtra(ROOT) != null) {// 如果是管理者
            hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity
            patientRecordList = viewModel.getRecord(hospitalEntity.hospitalName, hospitalEntity.number)
            binding.textTitle.text = hospitalEntity.hospitalName
            binding.textPatientNumber.text = hospitalEntity.number
        }else if (intent.getSerializableExtra(PATIENT) != null) {//如果是患者
            hospitalEntity = intent.getSerializableExtra(PATIENT) as HospitalEntity
            patientRecordList = viewModel.getRecord(hospitalEntity.hospitalName, hospitalEntity.number)
            binding.textTitle.text = hospitalEntity.hospitalName
            binding.textPatientNumber.text = hospitalEntity.number
            binding.imageEdit.visibility = View.INVISIBLE
        }

        viewModel.isShowCheckBox.observe(this) { newIds ->
            showCheckBox()
        }

        binding.recyclerCleanRecord.layoutManager = LinearLayoutManager(this)
        binding.recyclerCleanRecord.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        patientRecordAdapter = PatientRecordAdapter(this, patientRecordList, viewModel.isShowCheckBox)
        binding.recyclerCleanRecord.adapter = patientRecordAdapter


        // 註冊 BroadcastReceiver
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.updateRecycler(patientRecordAdapter, hospitalEntity.hospitalName, hospitalEntity.number)
            }
        }
        val intentFilter = IntentFilter(UPDATE_PATIENT_RECORD)
        registerReceiver(messageReceiver, intentFilter)

        binding.imageBack.setOnClickListener(this)
        binding.imageAdd.setOnClickListener(this)
        binding.imageDelete.setOnClickListener(this)
        binding.imageEdit.setOnClickListener(this)
    }

    fun showCheckBox() {
        if(viewModel.isShowCheckBox.value!!){
            binding.recyclerCleanRecord.adapter?.notifyDataSetChanged()
            binding.imageDelete.visibility = View.VISIBLE
            binding.imageAdd.visibility = View.INVISIBLE
        }else{
            binding.recyclerCleanRecord.adapter?.notifyDataSetChanged()
            binding.imageDelete.visibility = View.INVISIBLE
            binding.imageAdd.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                viewModel.back(this)
            }
            R.id.imageAdd -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(ROOT, hospitalEntity)
                startActivity(intent)
            }
            R.id.imageDelete -> {
                viewModel.deleteRecord(this)
            }
            R.id.imageEdit -> {
                val intent = Intent(this, EditPatientInformationActivity::class.java)
                intent.putExtra(ROOT, hospitalEntity)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                viewModel.back(this)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(messageReceiver)
    }
}