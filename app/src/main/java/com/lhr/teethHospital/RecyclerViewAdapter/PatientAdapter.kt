package com.lhr.teethHospital.RecyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.R
import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.util.ClassmateInformation.PatientInformationActivity
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment

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
        if(personalManagerFragment.isShowCheckBox){
            holder.checkboxDelete.visibility = View.VISIBLE
        }else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if (personalManagerFragment.isShowCheckBox) {
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if(holder.checkboxDelete.isChecked){
                    deleteList.add(arrayList[position])
                }else{
                    deleteList.remove(arrayList[position])
                }
            } else {
                val intent = Intent(mainActivity, PatientInformationActivity::class.java)
                intent.putExtra("hospitalEntity", arrayList[position])
                mainActivity.startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener {
            personalManagerFragment.presenter.showCheckBox()
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