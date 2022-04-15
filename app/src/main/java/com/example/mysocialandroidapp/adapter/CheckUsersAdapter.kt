package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.BuildConfig
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.CheckUserItemBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.util.ImageSetter
import com.example.mysocialandroidapp.util.loadCircleCrop

interface OnUserCheckListener {
    fun onCheckUser(user: User)
    fun isCheckboxVisible(user: User) : Boolean
    fun isCheckboxChecked(user: User) : Boolean
}


class CheckUsersAdapter (
    private val onUserCheckListener: OnUserCheckListener
) : ListAdapter<User, CheckUsersAdapter.CheckUserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckUserViewHolder {
        var binding =
            CheckUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CheckUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckUserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onUserCheckListener)
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


    class CheckUserViewHolder(
        private val binding: CheckUserItemBinding,
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: User, checkListener: OnUserCheckListener){
            binding.apply {
                username.text = user.name
                checked.isVisible = checkListener.isCheckboxVisible(user)
                checked.isChecked = checkListener.isCheckboxChecked(user)
                if (user.avatar != null) {
                    ImageSetter.set(avatar, user.avatar, circleCrop = true)
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_round)
                }
            }

            binding.checked.setOnClickListener {
                checkListener.onCheckUser(user)
            }
        }
    }
}