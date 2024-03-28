package com.lhr.teethHospital.util.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.WidgetTitleBarBinding

class TitleBarWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var binding: WidgetTitleBarBinding

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.widget_title_bar, this, true
        )
        attrs?.let {
            val attrs = context.obtainStyledAttributes(it, R.styleable.TitleBarWidget)

            // 設定標題
            setTitleText(attrs.getString(R.styleable.TitleBarWidget_titleText))
            setBack(attrs.getInt(R.styleable.TitleBarWidget_showBack, 1))
            setAdd(attrs.getInt(R.styleable.TitleBarWidget_showAdd, 1))

            attrs.recycle()
        }
    }

    fun setTitleText(title: CharSequence?) {
        binding.textTitle.text = title
    }

    fun setBack(isShow: Int) {
        binding.imageBack.visibility = if (isShow == 0) View.VISIBLE else View.GONE
    }

    fun setAdd(isShow: Int) {
        binding.imageAdd.visibility = if (isShow == 0) View.VISIBLE else View.GONE
    }
}