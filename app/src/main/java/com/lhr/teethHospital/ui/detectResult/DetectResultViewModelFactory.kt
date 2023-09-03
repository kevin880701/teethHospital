package com.lhr.teethHospital.ui.detectResult

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetectResultViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetectResultViewModel::class.java)) {
            return DetectResultViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}