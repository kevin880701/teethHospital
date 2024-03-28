package com.lhr.teethHospital.ui.main

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.ui.base.APP

class MainViewModel(context: Context) :
    AndroidViewModel(context.applicationContext as APP) {

    companion object {
        var isProgressBar: MutableLiveData<Boolean> =
            MutableLiveData<Boolean>().apply { value = false }
    }
}