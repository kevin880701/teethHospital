package com.lhr.teethHospital.ui.base

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class LoadState {
    object Loading : LoadState()
    data class Success(val data: List<String>) : LoadState()
    data class Error(val message: String) : LoadState()
    object Show : LoadState()
    object Hide : LoadState()
}

open class BaseViewModel(var context: Context) : AndroidViewModel(context.applicationContext as APP) {
    // 定义一个状态类，表示加载状态
    // LiveData 用于在加载状态改变时通知 UI
    private val _loadState = MutableLiveData<LoadState>()
    val loadState: MutableLiveData<LoadState> = _loadState

//    fun loadData() {
//        // 启动一个协程，在 IO 线程中加载数据
//        viewModelScope.launch(Dispatchers.IO) {
//            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//            // 更新加载状态为 Loading
//            _loadState.postValue(LoadState.Loading)
//
//            try {
//                // 模拟加载数据的耗时操作
//                delay(2000)
//                println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//
//                // 加载数据成功，更新加载状态为 Success，并传递加载的数据
//                val data = listOf("Item 1", "Item 2", "Item 3")
//                _loadState.postValue(LoadState.Success(data))
//            } catch (e: Exception) {
//                // 加载数据失败，更新加载状态为 Error，并传递错误信息
//                _loadState.postValue(LoadState.Error(e.message ?: "Unknown error"))
//            }
//        }
//    }

    fun showLoading() {
        _loadState.postValue(LoadState.Show)
    }

    fun hideLoading() {
        _loadState.postValue(LoadState.Hide)
    }

    fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}