package com.lhr.teethHospital.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        var isProgressBar: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
    }
}