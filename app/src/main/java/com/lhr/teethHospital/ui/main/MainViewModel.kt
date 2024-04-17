package com.lhr.teethHospital.ui.main

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseViewModel

class MainViewModel(context: Context) : BaseViewModel(context) {

    companion object {
        var isProgressBar: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
    }
}