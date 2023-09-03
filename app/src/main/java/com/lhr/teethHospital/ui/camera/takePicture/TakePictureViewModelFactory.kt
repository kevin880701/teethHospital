package com.lhr.teethHospital.ui.camera.takePicture

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.ui.camera.CameraViewModel

class TakePictureViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TakePictureViewModel::class.java)) {
            return TakePictureViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}