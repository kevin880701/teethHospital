package com.lhr.teethHospital.util.recyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.data.GroupInfo
import com.lhr.teethHospital.model.Model.Companion.hospitalInfoList
import com.lhr.teethHospital.model.Model.Companion.hospitalEntityList
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.ui.personalManager.PersonalManagerFragment
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.CLASS_INFO_LIST
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isShowCheckBox
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.recyclerInfoStatus
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.titleBarText
import java.util.ArrayList
import java.util.stream.Collectors

class PersonalManagerAdapter(personalManagerFragment: PersonalManagerFragment) :
    RecyclerView.Adapter<PersonalManagerAdapter.ViewHolder>() {
    var personalManagerFragment = personalManagerFragment
    var deleteList: ArrayList<GroupInfo> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_personal_manager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textGroupName.text = hospitalInfoList[position].groupName
        holder.textNumber.text = hospitalInfoList[position].quantity.toString() + "人"
        if (isShowCheckBox.value!!) {
            holder.checkboxDelete.visibility = View.VISIBLE
        } else {
            holder.checkboxDelete.visibility = View.GONE
            holder.checkboxDelete.isChecked = false
        }
        holder.itemView.setOnClickListener {
            if (isShowCheckBox.value!!) {
                holder.checkboxDelete.isChecked = !holder.checkboxDelete.isChecked
                if (holder.checkboxDelete.isChecked) {
                    deleteList.add(hospitalInfoList[position])
                } else {
                    deleteList.remove(hospitalInfoList[position])
                }
            } else {
                var list = hospitalEntityList.stream()
                    .filter { hospitalEntity -> hospitalEntity.hospitalName == hospitalInfoList[position].groupName }
                    .collect(
                        Collectors.toList()
                    ) as ArrayList<HospitalEntity>
                var classmateAdapter = PatientAdapter(list, personalManagerFragment)
                personalManagerFragment.binding.recyclerInfo.adapter = classmateAdapter
                titleBarText.postValue(hospitalInfoList[position].groupName)
                recyclerInfoStatus.value = CLASS_INFO_LIST
            }
        }
        holder.itemView.setOnLongClickListener {
            isShowCheckBox.value = true
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
        val textGroupName: TextView = v.findViewById(R.id.textName)
        val textNumber: TextView = v.findViewById(R.id.textQuantity)
        val checkboxDelete: CheckBox = v.findViewById(R.id.checkboxDelete)
    }

}