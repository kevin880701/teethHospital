package com.lhr.teethHospital.util.recyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.R
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.ui.patientInformation.PatientInformationActivity
import com.lhr.teethHospital.ui.personalManager.PersonalManagerFragment
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isShowCheckBox

class PatientAdapter(arrayList: ArrayList<HospitalEntity>, personalManagerFragment: PersonalManagerFragment) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>() {
    var personalManagerFragment = personalManagerFragment
    var arrayList = arrayList
    var deleteList: ArrayList<HospitalEntity> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textPatientNumber.text = arrayList[position].number
        if(isShowCheckBox.value!!){
            holder.checkboxDelete.visibility = View.VISIBLE
        }else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if (isShowCheckBox.value!!) { // 如果是長按刪除狀態
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if(holder.checkboxDelete.isChecked){
                    deleteList.add(arrayList[position])
                }else{
                    deleteList.remove(arrayList[position])
                }
            } else {
                val intent = Intent(personalManagerFragment.requireActivity(), PatientInformationActivity::class.java)
                intent.putExtra(ROOT, arrayList[position])
                personalManagerFragment.requireActivity().startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener {
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

    fun clearItems() {
        val oldSize = Model.hospitalInfoList.size
        Model.hospitalInfoList.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imageAvatar: ImageView = v.findViewById(R.id.imageAvatar)
        val textPatientNumber: TextView = v.findViewById(R.id.textPatientNumber)
        val textPatientName: TextView = v.findViewById(R.id.textPatientInformation)
        val checkboxDelete: CheckBox = v.findViewById(R.id.checkboxDelete)
    }
}