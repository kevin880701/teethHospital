package com.lhr.teethHospital.util.recyclerViewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.databinding.ItemMemberRecordBinding
import com.lhr.teethHospital.room.entity.RecordEntity

class MemberRecordAdapter(val listener: Listener): ListAdapter<RecordEntity, MemberRecordAdapter.ViewHolder>(LOCK_DIFF_UTIL) {

    companion object{
        val LOCK_DIFF_UTIL = object : DiffUtil.ItemCallback<RecordEntity>() {
            override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RecordEntity,
                newItem: RecordEntity
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }


    interface Listener{
        fun onItemClick(recordEntity: RecordEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMemberRecordBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(recordEntity: RecordEntity){
            binding.textRecordDate.text = recordEntity.recordDate
        }
    }
}