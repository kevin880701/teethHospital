package com.example.teethHospital.RecyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teethHospital.Model.Model.Companion.allFileList
import com.example.teethHospital.R
import com.example.teethHospital.util.History.HistoryActivity.Companion.historyActivity
import com.example.teethHospital.util.DetectResult.DetectResultActivity

class HistoryAdapter() : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)

//        val metrics = mContext.resources.displayMetrics
//        val widthPixels = metrics.heightPixels
//        val layoutParams = view.layoutParams
//        layoutParams.height = (widthPixels * 0.075).toInt()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textDate.text = allFileList[position].name

        holder.itemView.setOnClickListener {
            val intent = Intent(historyActivity, DetectResultActivity::class.java)
            intent.putExtra("fileName", allFileList[position].name)
            historyActivity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allFileList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textDate: TextView = v.findViewById(R.id.textDate)
    }
}