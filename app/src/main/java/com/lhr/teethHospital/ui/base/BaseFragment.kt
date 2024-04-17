package com.lhr.teethHospital.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber


abstract class BaseFragment: Fragment() {

    val viewModelFactory: AppViewModelFactory
        get() = (requireContext().applicationContext as APP).appContainer.viewModelFactory


    init {
        lifecycleScope.launchWhenStarted {
            APP.nightMode.distinctUntilChanged().observe(viewLifecycleOwner){ onNightModeChange(it) }
        }
    }

    protected open fun onNightModeChange(isNight: Boolean){}

    protected fun toast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private var logLifeCycle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (logLifeCycle) Timber.tag("fragmentLife").d("onCreate $this")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (logLifeCycle) Timber.tag("fragmentLife").d("onViewCreated $this")
    }

    override fun onStart() {
        super.onStart()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onStart $this")
    }

    override fun onResume() {
        super.onResume()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onResume $this")
    }

    override fun onPause() {
        super.onPause()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onPause $this")
    }

    override fun onStop() {
        super.onStop()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onStop $this")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onDestroyView $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onDestroy $this")
    }

}