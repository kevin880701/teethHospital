package com.lhr.teethHospital.ui.memberInformation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lhr.teethHospital.R
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.databinding.ActivityMemberInformationBinding
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.model.Model.Companion.PATIENT
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.ui.camera.CameraActivity
import com.lhr.teethHospital.ui.detectResult.DetectResultActivity
import com.lhr.teethHospital.ui.editPatientInformation.EditPatientInformationActivity
import com.lhr.teethHospital.util.recyclerViewAdapter.MemberRecordAdapter

class MemberInformationActivity : BaseActivity(), View.OnClickListener, MemberRecordAdapter.Listener {
    companion object{
        var UPDATE_PATIENT_RECORD = "UPDATE_PATIENT_RECORD"
    }
    override val viewModel: MemberInformationViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }
    private var _binding: ActivityMemberInformationBinding? = null
    private val binding get() = _binding!!
    lateinit var memberRecordAdapter: MemberRecordAdapter
    lateinit var hospitalEntity: HospitalEntity
    lateinit var patientRecordList: ArrayList<RecordEntity>
    lateinit var messageReceiver: BroadcastReceiver
    lateinit var repository: PersonalManagerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMemberInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = PersonalManagerRepository.getInstance(this)

        if (intent.getSerializableExtra(ROOT) != null) {// 如果是管理者
            hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity
            patientRecordList = viewModel.getMemberRecord(hospitalEntity.hospitalName, hospitalEntity.number)
            binding.titleBar.binding.textTitle.text = hospitalEntity.hospitalName
            binding.textPatientNumber.text = hospitalEntity.number
        }else if (intent.getSerializableExtra(PATIENT) != null) {//如果是患者
            hospitalEntity = intent.getSerializableExtra(PATIENT) as HospitalEntity
            patientRecordList = viewModel.getMemberRecord(hospitalEntity.hospitalName, hospitalEntity.number)
            binding.titleBar.binding.textTitle.text = hospitalEntity.hospitalName
            binding.textPatientNumber.text = hospitalEntity.number
            binding.titleBar.binding.imageAddPhoto.visibility = View.INVISIBLE
        }

        initView()
        bindViewModel()

//        viewModel.isShowCheckBox.observe(this) { newIds ->
//            showCheckBox()
//        }


        // 註冊 BroadcastReceiver
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                memberRecordAdapter.submitList(viewModel.getMemberRecord(hospitalEntity.hospitalName, hospitalEntity.number))
//                viewModel.updateRecycler(memberRecordAdapter, hospitalEntity.hospitalName, hospitalEntity.number)
            }
        }
        val intentFilter = IntentFilter(UPDATE_PATIENT_RECORD)
        registerReceiver(messageReceiver, intentFilter)
    }

    private fun initView(){
        initRecyclerView()

        binding.titleBar.binding.imageBack.setOnClickListener(this)
        binding.titleBar.binding.imageAddPhoto.setOnClickListener(this)
        binding.titleBar.binding.imageEdit.setOnClickListener(this)
    }



    private fun bindViewModel() {
        repository.memberRecordList.observe(this){ _ ->
            memberRecordAdapter.submitList(viewModel.getMemberRecord(hospitalEntity.hospitalName, hospitalEntity.number))
        }
    }

    private fun initRecyclerView() {
        memberRecordAdapter = MemberRecordAdapter(this)
        memberRecordAdapter.submitList(viewModel.getMemberRecord(hospitalEntity.hospitalName, hospitalEntity.number))
        binding.recyclerRecord.adapter = memberRecordAdapter
        binding.recyclerRecord.layoutManager = LinearLayoutManager(this)
    }


//    fun showCheckBox() {
//        if(viewModel.isShowCheckBox.value!!){
//            binding.recyclerCleanRecord.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.VISIBLE
//            binding.imageAdd.visibility = View.INVISIBLE
//        }else{
//            binding.recyclerCleanRecord.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.INVISIBLE
//            binding.imageAdd.visibility = View.VISIBLE
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                viewModel.back(this)
            }
            R.id.imageAddPhoto -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(ROOT, hospitalEntity)
                startActivity(intent)
            }
            R.id.imageDelete -> {
//                viewModel.deleteRecord(this)
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

    override fun onItemClick(recordEntity: RecordEntity) {
        val intent = Intent(this, DetectResultActivity::class.java)
        intent.putExtra("recordEntity", recordEntity)
        startActivity(intent)
    }
}