package com.lhr.teethHospital.ui.camera.detect

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentDetectBinding

class DetectFragment(PAGE_INDEX: String) : Fragment() {
    var PAGE_INDEX = PAGE_INDEX
    lateinit var binding: FragmentDetectBinding
    lateinit var viewModel: DetectViewModel
    var percent = -1.0F
    lateinit var messageReceiver: BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detect, container, false
        )
        val view: View = binding!!.root

        viewModel = ViewModelProvider(
            this,
            DetectViewModelFactory(this.requireActivity().application)
        )[DetectViewModel::class.java]
        binding.viewModel = viewModel

        return view
    }


    override fun onDestroy() {
        super.onDestroy()
        this.requireActivity().unregisterReceiver(messageReceiver)
    }
}