package com.lhr.teethHospital.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    // 保存用戶輸入的文本數據
    fun saveText(context: Context, key: String, text: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("baseUrl", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, text)
        editor.apply()
    }

    // 從 SharedPreferences 中獲取保存的文本數據
    fun getText(context: Context, key: String): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("baseUrl", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}