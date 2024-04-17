package com.lhr.teethHospital.ui.base

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {
    var imageBack: ImageView? = null

    protected abstract val viewModel: BaseViewModel // 假设有一个 viewModel 属性需要绑定

    var onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        //覆寫系統設定fontScale 1.3 to 1.0
        val config = newBase.resources.configuration
        config.fontScale = 1f
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private var logLifeCycle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onCreate $this")

    }

    private fun bindViewModel() {
        viewModel.loadState.observe(this) { state ->
            when (state) {
                is LoadState.Show -> {
                    showLoadingIndicator()
                }
                is LoadState.Hide -> {
                    hideLoadingIndicator()
                }
//                is LoadState.Success -> hideLoadingIndicator(state.data)
//                is LoadState.Error -> showError(state.message)
            }
        }
    }


    private var progressBar: ProgressBar? = null
    private var backgroundView: View? = null // 半透明的背景遮罩層
    private fun showLoadingIndicator() {
        // 如果進度條還沒有被初始化，則找到它
        if (progressBar == null) {
            // 創建進度條
            progressBar = ProgressBar(this)
            progressBar?.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            // 添加進度條到佈局中
            (window.decorView as? ViewGroup)?.addView(progressBar)

            // 創建背景遮罩層
            backgroundView = View(this)
            backgroundView?.setBackgroundColor(Color.parseColor("#80000000")) // 半透明的黑色背景
            backgroundView?.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // 添加背景遮罩層到佈局中
            (window.decorView as? ViewGroup)?.addView(backgroundView)

            // 設定背景遮罩層的點擊事件攔截
            backgroundView?.setOnClickListener { }
        }
        backgroundView?.visibility = View.VISIBLE
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        if (progressBar != null && ::backgroundView != null) {
            // 隱藏背景遮罩層和進度條
            backgroundView?.visibility = View.GONE
            progressBar?.visibility = View.GONE

            // 從佈局中移除背景遮罩層和進度條
            (window.decorView as? ViewGroup)?.removeView(backgroundView)
            (window.decorView as? ViewGroup)?.removeView(progressBar)

            progressBar = null
            backgroundView = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onStart $this")
    }

    override fun onResume() {
        super.onResume()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onResume $this")
    }

    override fun onPause() {
        super.onPause()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onPause $this")
    }

    override fun onStop() {
        super.onStop()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onStop $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (logLifeCycle) Timber.tag("ActivityLife").d("onDestroy $this")
    }

}