package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.BuildConfig
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.PostItemBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.util.loadCircleCrop

interface OnPostInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShowUsers(post: Post, userListType: UserListType){}
}

class PostsAdapter (
    private val onInteractionListener: OnPostInteractionListener,
    private val userId: Long
) : PagingDataAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, userId)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }



    class PostViewHolder(
        private val binding: PostItemBinding,
        private val onInteractionListener: OnPostInteractionListener,
        private val userId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                content.text = post.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                like.isChecked = post.likedByMe
                like.text = "${post.likeOwnerIds.size}"
                published.text = post.published
                link.text = post.link
                if (post.link.isNullOrBlank()) {
                    link.visibility = View.INVISIBLE
                }
                if (post.coords != null){
                    coords.text = "${post.coords!!.lat}° N, ${post.coords!!.long}° E"
                } else{
                    coords.visibility = View.INVISIBLE
                }
                if (attachment != null){
                    //TODO set attachment source
                }
                menu.visibility = View.VISIBLE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        menu.setGroupVisible(R.id.owned, userId == post.authorId)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }
                                R.id.likes -> {
                                    onInteractionListener.onShowUsers(post, UserListType.LIKES)
                                    true
                                }
                                R.id.mentions -> {
                                    onInteractionListener.onShowUsers(post, UserListType.MENTIONS)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                    like.isChecked = !like.isChecked
                }
            }

        }
    }
}