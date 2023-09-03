package com.lhr.teethHospital.ui.editPatientInformation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditPatientInformationViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPatientInformationViewModel::class.java)) {
            return EditPatientInformationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}