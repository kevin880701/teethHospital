package com.example.teethHospital.util.ClassmateInformation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teethHospital.Model.FileManager
import com.example.teethHospital.Model.Model.Companion.CleanRecordFilter
import com.example.teethHospital.Model.Model.Companion.UPDATE_CLEAN_RECORD
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.R
import com.example.teethHospital.RecyclerViewAdapter.PatientRecordAdapter
import com.example.teethHospital.Room.HospitalEntity
import com.example.teethHospital.Room.RecordEntity
import com.example.teethHospital.util.Camera.CameraActivity
import com.example.teethHospital.util.EditPatientInformation.EditPatientInformationActivity
import com.example.teethHospital.util.PatientInformationPresenter

class PatientInformationActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var presenter: PatientInformationPresenter
    lateinit var imageAvatar: ImageView
    lateinit var imageBack: ImageView
    lateinit var imageAdd: ImageView
    lateinit var imageDelete: ImageView
    lateinit var imageEdit: ImageView
    lateinit var textPatientNumber: TextView
    lateinit var recyclerCleanRecord: RecyclerView
    lateinit var patientRecordAdapter: PatientRecordAdapter
    lateinit var hospitalEntity: HospitalEntity
    lateinit var classRecordList: ArrayList<RecordEntity>
    var isShowCheckBox = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_information)
        supportActionBar!!.hide()
        presenter = PatientInformationPresenter(this)

        imageAvatar = findViewById(R.id.imageAvatar)
        imageBack = findViewById(R.id.imageBack)
        imageAdd = findViewById(R.id.imageAdd)
        imageDelete = findViewById(R.id.imageDelete)
        imageEdit = findViewById(R.id.imageEdit)
        textPatientNumber = findViewById(R.id.textPatientNumber)
        recyclerCleanRecord = findViewById(R.id.recyclerCleanRecord)

        hospitalEntity = intent.getSerializableExtra("hospitalEntity") as HospitalEntity
        classRecordList = presenter.getRecord(hospitalEntity.hospitalName, hospitalEntity.number)
        textPatientNumber.text = hospitalEntity.number

        recyclerCleanRecord.layoutManager = LinearLayoutManager(mainActivity)
        recyclerCleanRecord.addItemDecoration(
            DividerItemDecoration(
                mainActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        patientRecordAdapter = PatientRecordAdapter(this, classRecordList)
        recyclerCleanRecord.adapter = patientRecordAdapter

        registerReceiver(mainBroadcastReceiver, IntentFilter(CleanRecordFilter))

        imageBack.setOnClickListener(this)
        imageAdd.setOnClickListener(this)
        imageDelete.setOnClickListener(this)
        imageEdit.setOnClickListener(this)
    }

    var mainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.getStringExtra("action")) {
                UPDATE_CLEAN_RECORD -> {
                    presenter.updateRecycler(hospitalEntity.hospitalName, hospitalEntity.number)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                presenter.back()
            }
            R.id.imageAdd -> {
                val intent = Intent(mainActivity, CameraActivity::class.java)
                intent.putExtra("hospitalEntity", hospitalEntity)
                mainActivity.startActivity(intent)
            }
            R.id.imageDelete -> {
                presenter.deleteRecord()
            }
            R.id.imageEdit -> {
                val intent = Intent(mainActivity, EditPatientInformationActivity::class.java)
                intent.putExtra("hospitalEntity", hospitalEntity)
                mainActivity.startActivity(intent)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                presenter.back()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}