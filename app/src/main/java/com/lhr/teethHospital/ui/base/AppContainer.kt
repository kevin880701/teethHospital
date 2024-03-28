package com.lhr.teethHospital.ui.base

import android.content.Context
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class AppContainer(private val context: Context) {

//    val regionRepository by lazy { RegionRepository.getInstance(context) }
//
//    val formRepository by lazy { FormRepository.getInstance(context) }


    val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            context,
//            regionRepository,
//            formRepository
        )
    }
}
