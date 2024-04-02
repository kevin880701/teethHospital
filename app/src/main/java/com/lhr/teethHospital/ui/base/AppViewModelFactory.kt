package com.lhr.teethHospital.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.ui.camera.CameraViewModel
import com.lhr.teethHospital.ui.main.MainViewModel
import com.lhr.teethHospital.ui.memberInformation.MemberInformationViewModel
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel
import com.lhr.teethHospital.ui.setting.SettingViewModel

class AppViewModelFactory(
    private val context: Context,
    private val personalManagerRepository: PersonalManagerRepository,
    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(context) as T
            PersonalManagerViewModel::class.java -> PersonalManagerViewModel(context, personalManagerRepository) as T
            MemberInformationViewModel::class.java -> MemberInformationViewModel(context, personalManagerRepository) as T
            CameraViewModel::class.java -> CameraViewModel(context, personalManagerRepository) as T
            SettingViewModel::class.java -> SettingViewModel(context) as T
            else -> throw IllegalArgumentException("not support")
        }
    }
}