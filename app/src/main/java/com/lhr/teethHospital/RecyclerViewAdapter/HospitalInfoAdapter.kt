package com.lhr.teethHospital.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.Model.HospitalInfo
import com.lhr.teethHospital.Model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.Model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.R
import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment.Companion.CLASSMATE_LIST
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment.Companion.recyclerInfoStatus
import java.util.ArrayList
import java.util.stream.Collectors

class HospitalInfoAdapter(personalManagerFragment: PersonalManagerFragment) :
    RecyclerView.Adapter<HospitalInfoAdapter.ViewHolder>() {
    var personalManagerFragment = personalManagerFragment
    var deleteList: ArrayList<HospitalInfo> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hospital, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textClassName.text = hospitalInfoList[position].className
        holder.textNumber.text = hospitalInfoList[position].number.toString() + "人"
        if (personalManagerFragment.isShowCheckBox) {
            holder.checkboxDelete.visibility = View.VISIBLE
        } else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if (personalManagerFragment.isShowCheckBox) {
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if (holder.checkboxDelete.isChecked) {
                    deleteList.add(hospitalInfoList[position])
                } else {
                    deleteList.remove(hospitalInfoList[position])
                }
            } else {
                var list = hospitalEntityList.stream()
                    .filter { classEntity -> classEntity.hospitalName == hospitalInfoList[position].className }.collect(
                    Collectors.toList()
                ) as ArrayList<HospitalEntity>
                var classmateAdapter = PatientAdapter(list, personalManagerFragment)
                personalManagerFragment.recyclerInfo.adapter = classmateAdapter
                personalManagerFragment.textClassName.text = hospitalInfoList[position].className
                personalManagerFragment.constrainClassmate.visibility = View.VISIBLE
                personalManagerFragment.textClassInfo.visibility = View.INVISIBLE
                recyclerInfoStatus = CLASSMATE_LIST
            }
        }
        holder.itemView.setOnLongClickListener {
            personalManagerFragment.presenter.showCheckBox()
            holder.checkboxDelete.isChecked = true
            deleteList.add(hospitalInfoList[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return hospitalInfoList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun clearItems() {
        val oldSize = hospitalInfoList.size
        hospitalInfoList.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textClassName: TextView = v.findViewById(R.id.textClassName)
        val textNumber: TextView = v.findViewById(R.id.textNumber)
        val checkboxDelete: CheckBox = v.findViewById(R.id.checkboxDelete)
    }
}