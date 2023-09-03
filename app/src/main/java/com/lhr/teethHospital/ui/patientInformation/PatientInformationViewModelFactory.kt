package com.lhr.teethHospital.ui.patientInformation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.ui.main.MainViewModel

class PatientInformationViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientInformationViewModel::class.java)) {
            return PatientInformationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}