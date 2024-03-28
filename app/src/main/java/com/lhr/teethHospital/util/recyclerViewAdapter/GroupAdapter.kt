package com.lhr.teethHospital.util.recyclerViewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.data.GroupInfo
import com.lhr.teethHospital.databinding.ItemPersonalManagerBinding

class GroupAdapter(val listener: Listener, context: Context) :
    ListAdapter<GroupInfo, GroupAdapter.ViewHolder>(LOCK_DIFF_UTIL) {
    var context = context

    companion object {
        val LOCK_DIFF_UTIL = object : DiffUtil.ItemCallback<GroupInfo>() {
            override fun areItemsTheSame(oldItem: GroupInfo, newItem: GroupInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GroupInfo,
                newItem: GroupInfo
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPersonalManagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPersonalManagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupInfo: GroupInfo) {
            binding.textName.text = groupInfo.groupName
            binding.textQuantity.text = groupInfo.quantity.toString()

            binding.root.setOnClickListener {
                listener.onItemClick(getItem(adapterPosition))
            }
        }
    }

    interface Listener {
        fun onItemClick(item: GroupInfo)
    }
}