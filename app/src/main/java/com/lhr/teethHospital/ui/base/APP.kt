package com.lhr.teethHospital.ui.base

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.BuildConfig
import com.lhr.teethHospital.room.SqlDatabase
import timber.log.Timber

class APP: Application() {

    val appContainer = AppContainer(this)

    override fun onCreate() {
        super.onCreate()

        SqlDatabase.init(this)
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }else{
//            Timber.plant(CrashlyticsTree())
        }

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.e(e)
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    companion object{
//        var navAction: NavigationAction? = null
        val nightMode = MutableLiveData(false)
    }

}