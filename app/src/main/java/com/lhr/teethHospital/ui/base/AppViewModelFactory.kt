package com.lhr.teethHospital.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.ui.main.MainViewModel
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel

class AppViewModelFactory(
    private val context: Context,
    private val personalManagerRepository: PersonalManagerRepository,
//    private val formRepository: FormRepository,

    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(context) as T
            PersonalManagerViewModel::class.java -> PersonalManagerViewModel(context) as T
            else -> throw IllegalArgumentException("not support")
        }
    }
}