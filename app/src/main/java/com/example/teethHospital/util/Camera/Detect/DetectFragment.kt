package com.example.teethHospital.util.Camera.Detect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.teethHospital.R

class DetectFragment: Fragment() {

    lateinit var imageOriginal: ImageView
    lateinit var imageAfter: ImageView
    lateinit var imageLight: ImageView
    lateinit var textPercent: TextView
    lateinit var presenter: DetectPresenter
    var percent = 0.0F

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_detect_result, container, false)
        presenter = DetectPresenter(this)
        imageOriginal = view.findViewById(R.id.imageOriginal)
        imageAfter = view.findViewById(R.id.imageAfter)
        imageLight = view.findViewById(R.id.imageLight)
        textPercent = view.findViewById(R.id.textPercent)

        return view
    }
}