package com.lhr.teethHospital.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.ActivityLoginBinding
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.PATIENT
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.spinnerAdapter.HospitalNameSpinnerAdapter
import com.lhr.teethHospital.ui.main.MainActivity
import com.lhr.teethHospital.ui.patientInformation.PatientInformationActivity


class LoginActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener, View.OnClickListener {

    lateinit var viewModel: LoginViewModel
    lateinit var binding: ActivityLoginBinding
    var currentSpinnerText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(this.application)
        )[LoginViewModel::class.java]
        binding.viewModel = viewModel

        val adapter = HospitalNameSpinnerAdapter(this, hospitalInfoList)
        binding.spinnerHospitalName.adapter = adapter
        // 設定spinner預設值
        binding.spinnerHospitalName.setSelection(0) // defaultPosition 是默认项的索引

        binding.spinnerHospitalName.onItemSelectedListener = this
        binding.buttonLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLogin -> {
                //用於檢查是否有資料
                val found = hospitalEntityList.find { it.hospitalName == currentSpinnerText && it.number == binding.editNumber.text.toString() }
//                val found = hospitalEntityList.any { it.hospitalName == currentSpinnerText && it.number == binding.editNumber.text.toString() }
                if (binding.editNumber.text.toString() == "1111") { // 管理員登入
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if(found != null){
                    val intent = Intent(this, PatientInformationActivity::class.java)
                    intent.putExtra(PATIENT, found)
                    startActivity(intent)
                }else{
                    // 沒有對應的數據
                    Toast.makeText(this, "資料輸入錯誤", Toast.LENGTH_SHORT).show()
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // 獲取當前選擇的內容
        currentSpinnerText = hospitalInfoList[position].hospitalName

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //spinner沒有選擇時
    }
}