package com.example.teethHospital.RecyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teethHospital.R
import com.example.teethHospital.Room.RecordEntity
import com.example.teethHospital.util.ClassmateInformation.PatientInformationActivity
import com.example.teethHospital.util.DetectResult.DetectResultActivity

class PatientRecordAdapter(classmateInformationActivity: PatientInformationActivity, arrayList: ArrayList<RecordEntity>) : RecyclerView.Adapter<PatientRecordAdapter.ViewHolder>() {
    var arrayList = arrayList
    var deleteList: ArrayList<RecordEntity> = ArrayList()
    var classmateInformationActivity = classmateInformationActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dateArray = arrayList[position].recordDate.split("-")
        holder.textRecordDate.text = "${dateArray[1]}年${dateArray[2]}月${dateArray[3]}日 ${dateArray[4]}點${dateArray[5]}分${dateArray[6]}秒"
        if(classmateInformationActivity.isShowCheckBox){
            holder.checkboxDelete.visibility = View.VISIBLE
        }else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if(classmateInformationActivity.isShowCheckBox){
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if(holder.checkboxDelete.isChecked){
                    deleteList.add(arrayList[position])
                }else{
                    deleteList.remove(arrayList[position])
                }
            }else {
                val intent = Intent(classmateInformationActivity, DetectResultActivity::class.java)
                intent.putExtra("recordEntity", arrayList[position])
                classmateInformationActivity.startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener{
            classmateInformationActivity.presenter.showCheckBox()
            holder.checkboxDelete.isChecked = true
            deleteList.add(arrayList[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textRecordDate: TextView = v.findViewById(R.id.textRecordDate)
        val checkboxDelete: CheckBox = v.findViewById(R.id.checkboxDelete)
    }
}