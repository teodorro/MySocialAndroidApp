package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp.BuildConfig
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.PostItemBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.util.DateStringFormatter
import com.example.mysocialandroidapp.util.loadCircleCrop

interface OnPostInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShowUsers(post: Post, userListType: UserListType){}
    fun onUserClick(post: Post) {}
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
                if (post.authorAvatar != null) {
                    avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_round)
                }
                like.isChecked = post.likedByMe
                like.text = "${post.likeOwnerIds.size}"
                published.text = DateStringFormatter.getDateTimeFromInstance(post.published)
                link.text = post.link
                if (post.link.isNullOrBlank()) {
                    link.visibility = View.GONE
                }
                if (post.coords != null){
                    coords.text = "http://www.google.com/maps/place/${post.coords!!.lat},${post.coords!!.long}"
                } else{
                    coordsLine.visibility = View.GONE
                }
                post.attachment?.let {
                    if (it.type == AttachmentType.IMAGE) {
                        Glide.with(attachment)
                            .load(post.attachment?.url)
                            .placeholder(R.drawable.ic_baseline_downloading_24)
                            .error(R.drawable.ic_baseline_error_24)
                            .timeout(10_000)
                            .into(attachment)
//                    imageViewAttachment.setOnClickListener {
//                        onInteractionListener.onShowPicAttachment(post)
//                    }
                    }
                }

                author.setOnClickListener {
                    onInteractionListener.onUserClick(post)
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