package com.lhr.teethHospital.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.ui.main.MainViewModel

class AppViewModelFactory(
    private val context: Context,
//    private val regionRepository: RegionRepository,
//    private val formRepository: FormRepository,

    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(context) as T
            else -> throw IllegalArgumentException("not support")
        }
    }
}