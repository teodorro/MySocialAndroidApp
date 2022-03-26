package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.databinding.UserItemBinding
import com.example.mysocialandroidapp.dto.User

interface OnUserClickListener {
    fun onUserClicked(user: User)
}


class UsersAdapter (
    private val onUserClickListener: OnUserClickListener
) : ListAdapter<User, UsersAdapter.UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var binding =
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onUserClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }


    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }


    class UserViewHolder(
        private val binding: UserItemBinding,
    ) : RecyclerView.ViewHolder(binding.root){
        var userId: Long = -1
        fun bind(user: User, clickListener: OnUserClickListener){
            binding.apply {
                username.text = user.name
                userId = user.id
            }

            itemView.setOnClickListener {
                clickListener.onUserClicked(user)
            }
        }
    }
}