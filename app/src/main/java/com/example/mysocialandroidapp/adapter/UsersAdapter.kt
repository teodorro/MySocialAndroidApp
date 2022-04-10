package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.BuildConfig
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.UserItemBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.util.loadCircleCrop

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
        fun bind(user: User, clickListener: OnUserClickListener){
            binding.apply {
                username.text = user.name
                if (user.avatar != null) {
                    avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${user.avatar}")
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_round)
                }
            }

            itemView.setOnClickListener {
                clickListener.onUserClicked(user)
            }
        }
    }
}