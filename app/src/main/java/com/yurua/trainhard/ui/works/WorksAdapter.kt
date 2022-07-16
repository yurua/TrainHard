package com.yurua.trainhard.ui.works

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.databinding.ItemWorkBinding
import com.yurua.trainhard.ui.works.WorksAdapter.WorksViewHolder
import com.yurua.trainhard.util.getGroups

class WorksAdapter(private val listener: OnItemClickListener) :
  ListAdapter<Work, WorksViewHolder>(DiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
    val binding = ItemWorkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return WorksViewHolder(binding)
  }

  override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
    val currentItem = getItem(position)
    holder.bind(currentItem)
  }

  inner class WorksViewHolder(private val binding: ItemWorkBinding) : ViewHolder(binding.root) {

    init {
      binding.apply {
        root.setOnClickListener {
          val position = adapterPosition
          if (position != RecyclerView.NO_POSITION) {
            val work = getItem(position)
            listener.onItemClick(work)
          }
        }
      }
    }

    fun bind(work: Work) {
      binding.apply {
        val groups = getGroups(work)
        chipGroup1.text = if (groups.size == 1) groups[0] else "${groups[0]}  ·  ${groups[1]}"
        workoutDate.text = work.date
      }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(work: Work)
  }

  class DiffCallback : ItemCallback<Work>() {
    override fun areItemsTheSame(oldItem: Work, newItem: Work) =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Work, newItem: Work) =
      oldItem == newItem
  }

}