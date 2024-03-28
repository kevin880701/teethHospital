package com.lhr.teethHospital.spinnerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lhr.teethHospital.R
import com.lhr.teethHospital.data.GroupInfo

class HospitalNameSpinnerAdapter(
    private val context: Context,
    private val data: ArrayList<GroupInfo>
) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_hospital_name, parent, false)

        val hospitalInfo = getItem(position) as GroupInfo
        val textView = view.findViewById<TextView>(R.id.textHospitalName)
        textView.text = hospitalInfo.groupName

        return view
    }

    // 如果需要自定义下拉列表项的样式，可以实现 getDropDownView() 方法
}