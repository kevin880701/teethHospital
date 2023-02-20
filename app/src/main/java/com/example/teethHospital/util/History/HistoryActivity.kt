package com.example.teethHospital.util.History

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teethHospital.R
import com.example.teethHospital.RecyclerViewAdapter.HistoryAdapter


class HistoryActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        lateinit var historyActivity: HistoryActivity
    }
    lateinit var recyclerHistory: RecyclerView
    lateinit var historyAdapter: HistoryAdapter
    lateinit var imageBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar!!.hide()

        historyActivity = this
        imageBack = findViewById(R.id.imageBack)
        recyclerHistory = findViewById(R.id.recyclerHistory)

        recyclerHistory.layoutManager = LinearLayoutManager(this)
        recyclerHistory.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        historyAdapter = HistoryAdapter()
        recyclerHistory.adapter = historyAdapter

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