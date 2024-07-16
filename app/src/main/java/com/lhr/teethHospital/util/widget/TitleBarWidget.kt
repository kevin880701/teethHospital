package com.lhr.teethHospital.util.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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
            setBack(attrs.getInt(R.styleable.TitleBarWidget_showBack, 8))
            setAdd(attrs.getInt(R.styleable.TitleBarWidget_showAdd, 8))
            setAddPhoto(attrs.getInt(R.styleable.TitleBarWidget_showCamera, 8))
            setEdit(attrs.getInt(R.styleable.TitleBarWidget_showEdit, 8))
            setSave(attrs.getInt(R.styleable.TitleBarWidget_showSave, 8))
            setDelete(attrs.getInt(R.styleable.TitleBarWidget_showDelete, 8))

            attrs.recycle()
        }
    }

    fun setTitleText(title: CharSequence?) {
        binding.textTitle.text = title
    }

    fun setBack(visibility: Int) {
        binding.imageBack.visibility = visibility
    }

    fun setAdd(visibility: Int) {
        binding.imageAdd.visibility = visibility
    }

    fun setAddPhoto(visibility: Int) {
        binding.imageAddPhoto.visibility = visibility
    }

    fun setEdit(visibility: Int) {
        binding.imageEdit.visibility = visibility
    }

    fun setSave(visibility: Int) {
        binding.imageSave.visibility = visibility
    }

    fun setDelete(visibility: Int) {
        binding.imageDelete.visibility = visibility
    }
}