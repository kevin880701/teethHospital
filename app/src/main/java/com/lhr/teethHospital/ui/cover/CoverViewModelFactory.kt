package com.lhr.teethHospital.ui.cover

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.ui.main.MainViewModel

class CoverViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoverViewModel::class.java)) {
            return CoverViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}