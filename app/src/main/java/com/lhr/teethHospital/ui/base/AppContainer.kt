package com.lhr.teethHospital.ui.base

import android.content.Context
import com.lhr.teethHospital.data.PersonalManagerRepository
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class AppContainer(private val context: Context) {

    val personalManagerRepository by lazy { PersonalManagerRepository.getInstance(context) }

    val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            context,personalManagerRepository
        )
    }
}
