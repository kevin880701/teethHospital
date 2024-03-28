package com.lhr.teethHospital.ui.editPatientInformation

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.model.FileManager
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.databinding.ActivityEditPatientInformationBinding
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.ui.memberInformation.MemberInformationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EditPatientInformationActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var viewModel: EditPatientInformationViewModel
    lateinit var binding: ActivityEditPatientInformationBinding
    lateinit var hospitalEntity: HospitalEntity
    val fileManager = FileManager()
    var state = "IMPORT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_patient_information)
        viewModel = ViewModelProvider(
            this,
            EditPatientInformationViewModelFactory(this.application)
        )[EditPatientInformationViewModel::class.java]
        binding.viewModel = viewModel

        if (intent.getSerializableExtra(ROOT) != null) {
            hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity
            binding.editHospitalName.setText(hospitalEntity.hospitalName)
            binding.editPatientNumber.setText(hospitalEntity.number)
            binding.editGender.setText(hospitalEntity.gender)
            binding.editBirthday.setText(hospitalEntity.birthday)
            state = "EDIT"
        }

        binding.imageBack.setOnClickListener(this)
        binding.buttonSaveInformation.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }
            R.id.buttonSaveInformation -> {
                if (state == "EDIT") {
                    if (((binding.editHospitalName.text.toString() == hospitalEntity.hospitalName) || (binding.editPatientNumber.text.toString() == hospitalEntity.number)) &&
                        hospitalEntityList.stream().filter { o ->
                            (o.hospitalName == binding.editHospitalName.text.toString()) && (o.number == binding.editPatientNumber.text.toString())
                        }.findFirst().isPresent
                    ) {
                        Toast.makeText(this, "病歷號重覆", Toast.LENGTH_SHORT).show()
                    } else {
                        runBlocking {     // 阻塞主執行緒
                            launch(Dispatchers.IO) {
                                SqlDatabase.getInstance().getHospitalDao().updatePatientInformation(
                                    hospitalEntity.hospitalName,
                                    hospitalEntity.number,
                                    binding.editHospitalName.text.toString(),
                                    binding.editPatientNumber.text.toString(),
                                    binding.editGender.text.toString(),
                                    binding.editBirthday.text.toString()
                                )
                                fileManager.updateFileName(
                                    hospitalEntity.hospitalName, hospitalEntity.number,
                                    binding.editHospitalName.text.toString(), binding.editPatientNumber.text.toString(),
                                    this@EditPatientInformationActivity
                                )
                                SqlDatabase.getInstance().getRecordDao().updateRecord(
                                    hospitalEntity.hospitalName,
                                    hospitalEntity.number,
                                    binding.editHospitalName.text.toString(),
                                    binding.editPatientNumber.text.toString(),
                                    binding.editGender.text.toString(),
                                    binding.editBirthday.text.toString()
                                )
                            }
                        }
                        var newHospitalEntity = HospitalEntity()
                        newHospitalEntity.hospitalName = binding.editHospitalName.text.toString()
                        newHospitalEntity.number = binding.editPatientNumber.text.toString()
                        newHospitalEntity.gender = binding.editGender.text.toString()
                        newHospitalEntity.birthday = binding.editBirthday.text.toString()
                        val intent = Intent(this, MemberInformationActivity::class.java)
                        intent.putExtra(ROOT, newHospitalEntity)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runBlocking {     // 阻塞主執行緒
                        launch(Dispatchers.IO) {
                            SqlDatabase.getInstance().getHospitalDao().importPatientInformation(
                                binding.editHospitalName.text.toString(),
                                binding.editPatientNumber.text.toString(),
                                binding.editGender.text.toString(),
                                binding.editBirthday.text.toString()
                            )
                        }
                    }
                    val intent = Intent("updateRecyclerInfo")
                    sendBroadcast(intent)
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