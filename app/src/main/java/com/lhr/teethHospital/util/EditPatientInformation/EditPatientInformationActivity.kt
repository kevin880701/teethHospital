package com.lhr.teethHospital.util.EditPatientInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lhr.teethHospital.Model.FileManager
import com.lhr.teethHospital.Model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.Model.Model.Companion.personalManagerFragment
import com.lhr.teethHospital.R
import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.Room.SqlDatabase
import com.lhr.teethHospital.util.ClassmateInformation.PatientInformationActivity
import com.lhr.teethHospital.util.EditPatientInformationPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EditPatientInformationActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var presenter: EditPatientInformationPresenter
    lateinit var imageBack: ImageView
    lateinit var imageAvatar: ImageView
    lateinit var editHospitalName: EditText
    lateinit var editPatientNumber: EditText
    lateinit var editGender: EditText
    lateinit var editBirthday: EditText
    lateinit var buttonSaveInformation: Button
    lateinit var dataBase: SqlDatabase
    lateinit var hospitalEntity: HospitalEntity
    val fileManager = FileManager()
    var state = "IMPORT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_patient_information)
        supportActionBar!!.hide()

        imageBack = findViewById(R.id.imageBack)
        imageAvatar = findViewById(R.id.imageAvatar)
        editHospitalName = findViewById(R.id.editHospitalName)
        editPatientNumber = findViewById(R.id.editPatientNumber)
        editGender = findViewById(R.id.editGender)
        editBirthday = findViewById(R.id.editBirthday)
        buttonSaveInformation = findViewById(R.id.buttonSaveInformation)

        if (intent.getSerializableExtra("hospitalEntity") != null) {
            hospitalEntity = intent.getSerializableExtra("hospitalEntity") as HospitalEntity
            editHospitalName.setText(hospitalEntity.hospitalName)
            editPatientNumber.setText(hospitalEntity.number)
            editGender.setText(hospitalEntity.gender)
            editBirthday.setText(hospitalEntity.birthday)
            state = "EDIT"
        }


        presenter = EditPatientInformationPresenter(this)
        dataBase = SqlDatabase(this)

        imageBack.setOnClickListener(this)
        buttonSaveInformation.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }
            R.id.buttonSaveInformation -> {
                if (state == "EDIT") {
                    if (((editHospitalName.text.toString() == hospitalEntity.hospitalName) || (editPatientNumber.text.toString() == hospitalEntity.number)) &&
                        hospitalEntityList.stream().filter { o ->
                            (o.hospitalName == editHospitalName.text.toString()) && (o.number == editPatientNumber.text.toString())
                        }.findFirst().isPresent
                    ) {
                        Toast.makeText(mainActivity, "病歷號重覆", Toast.LENGTH_SHORT).show()
                    } else {
                        runBlocking {     // 阻塞主執行緒
                            launch(Dispatchers.IO) {
                                dataBase.getHospitalDao().updatePatientInformation(
                                    hospitalEntity.hospitalName,
                                    hospitalEntity.number,
                                    editHospitalName.text.toString(),
                                    editPatientNumber.text.toString(),
                                    editGender.text.toString(),
                                    editBirthday.text.toString()
                                )
                                fileManager.updateFileName(
                                    hospitalEntity.hospitalName, hospitalEntity.number,
                                    editHospitalName.text.toString(), editPatientNumber.text.toString()
                                )
                                dataBase.getRecordDao().updateRecord(
                                    hospitalEntity.hospitalName,
                                    hospitalEntity.number,
                                    editHospitalName.text.toString(),
                                    editPatientNumber.text.toString(),
                                    editGender.text.toString(),
                                    editBirthday.text.toString()
                                )
                            }
                        }
                        personalManagerFragment.presenter.updateRecyclerInfo()
                        var newHospitalEntity = HospitalEntity()
                        newHospitalEntity.hospitalName = editHospitalName.text.toString()
                        newHospitalEntity.number = editPatientNumber.text.toString()
                        newHospitalEntity.gender = editGender.text.toString()
                        newHospitalEntity.birthday = editBirthday.text.toString()
                        val intent = Intent(mainActivity, PatientInformationActivity::class.java)
                        intent.putExtra("hospitalEntity", newHospitalEntity)
                        mainActivity.startActivity(intent)
                        finish()
                    }
                } else {
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            dataBase.getHospitalDao().importPatientInformation(
                                editHospitalName.text.toString(),
                                editPatientNumber.text.toString(),
                                editGender.text.toString(),
                                editBirthday.text.toString()
                            )
                        }
                    }
                    personalManagerFragment.presenter.updateRecyclerInfo()
                    finish()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}