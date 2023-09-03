package com.lhr.teethHospital.recyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.ui.patientInformation.PatientInformationActivity
import com.lhr.teethHospital.ui.detectResult.DetectResultActivity
import com.lhr.teethHospital.ui.patientInformation.PatientInformationViewModel

class PatientRecordAdapter(patientInformationActivity: PatientInformationActivity, arrayList: ArrayList<RecordEntity>, isShowCheckBox: MutableLiveData<Boolean>) : RecyclerView.Adapter<PatientRecordAdapter.ViewHolder>() {
    var arrayList = arrayList
    var isShowCheckBox = isShowCheckBox
    var deleteList: ArrayList<RecordEntity> = ArrayList()
    var patientInformationActivity = patientInformationActivity
    lateinit var viewModel: PatientInformationViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dateArray = arrayList[position].recordDate.split("-")
        holder.textRecordDate.text = "${dateArray[1]}年${dateArray[2]}月${dateArray[3]}日 ${dateArray[4]}點${dateArray[5]}分${dateArray[6]}秒"
        if(isShowCheckBox.value!!){
            holder.checkboxDelete.visibility = View.VISIBLE
        }else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if(isShowCheckBox.value!!){
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if(holder.checkboxDelete.isChecked){
                    deleteList.add(arrayList[position])
                }else{
                    deleteList.remove(arrayList[position])
                }
            }else {
                val intent = Intent(patientInformationActivity, DetectResultActivity::class.java)
                intent.putExtra("recordEntity", arrayList[position])
                patientInformationActivity.startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener{
            isShowCheckBox.value = true
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