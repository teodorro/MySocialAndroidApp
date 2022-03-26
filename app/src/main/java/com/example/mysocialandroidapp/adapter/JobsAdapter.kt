package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.JobItemBinding
import com.example.mysocialandroidapp.dto.Job

interface OnJobInteractionListener {
    fun onEdit(job: Job) {}
    fun onRemove(job: Job) {}
}


class JobsAdapter(
    private val onInteractionListener: OnJobInteractionListener,
    private val userId: Long
) : ListAdapter<Job, JobsAdapter.JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        var binding =
            JobItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return JobViewHolder(binding, onInteractionListener, userId)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }


    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }


    class JobViewHolder(
        private val binding: JobItemBinding,
        private val onInteractionListener: OnJobInteractionListener,
        private val userId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(job: Job) {
            binding.apply {
                jobname.text = job.name
                position.text = job.position
                menu.visibility = View.VISIBLE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_job)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(job)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(job)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
            }

        }
    }
}