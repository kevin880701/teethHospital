package com.example.teethHospital.util.Teach

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.teethHospital.R


class TeachActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var webView: WebView
    lateinit var imageBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teach)
        supportActionBar!!.hide()

        webView = findViewById(R.id.webView)
        imageBack = findViewById(R.id.imageBack)

        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.loadUrl("https://www.youtube.com/embed/kAlzoyAXok4")

        imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { //捕捉返回鍵
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}