package com.example.teethHospital.util.Setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teethHospital.R

class SettingFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_setting, container, false)


        return view
    }

    override fun onClick(p0: View?) {
    }
}