package com.lhr.teethHospital.ui.personalManager

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PersonalManagerViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalManagerViewModel::class.java)) {
            return PersonalManagerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}