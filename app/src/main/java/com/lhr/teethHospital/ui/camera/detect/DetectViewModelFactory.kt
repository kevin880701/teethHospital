package com.lhr.teethHospital.ui.camera.detect

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetectViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetectViewModel::class.java)) {
            return DetectViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}