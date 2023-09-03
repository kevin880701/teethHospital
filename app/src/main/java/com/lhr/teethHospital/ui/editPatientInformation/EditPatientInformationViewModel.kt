package com.lhr.teethHospital.ui.editPatientInformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.lhr.teethHospital.data.editPatientInformation.EditPatientInformationRepository

class EditPatientInformationViewModel(application: Application) : AndroidViewModel(application) {

    var editPatientInformationRepository = EditPatientInformationRepository(application)
}