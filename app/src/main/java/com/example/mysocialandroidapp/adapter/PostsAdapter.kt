package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.PostItemBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.observers.MediaLifecycleObserver
import com.example.mysocialandroidapp.util.DateStringFormatter
import com.example.mysocialandroidapp.util.ImageSetter

interface OnPostInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShowUsers(post: Post, userListType: UserListType){}
    fun onUserClick(post: Post) {}
}



class PostsAdapter (
    private val onInteractionListener: OnPostInteractionListener,
    private val userId: Long,
    private val mediaObserver: MediaLifecycleObserver,
) : PagingDataAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, userId, mediaObserver)
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
        private val mediaObserver: MediaLifecycleObserver,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                content.text = post.content
                if (post.authorAvatar != null) {
                    ImageSetter.set(avatar, post.authorAvatar, circleCrop = true)
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_round)
                }
                like.isChecked = post.likedByMe
                like.text = "${post.likeOwnerIds.size}"
                published.text = DateStringFormatter.getDateTimeFromInstance(post.published)
                link.text = post.link
                if (post.link.isNullOrBlank()) {
                    link.visibility = View.GONE
                } else{
                    link.visibility = View.VISIBLE
                }
                if (post.coords != null){
                    coords.text = "http://www.google.com/maps/place/${post.coords!!.lat},${post.coords!!.long}"
                    coordsLine.visibility = View.VISIBLE
                } else{
                    coordsLine.visibility = View.GONE
                }
                post.attachment?.let {
                    if (it.type == AttachmentType.IMAGE) {
                        ImageSetter.set(imageAttachment, it.url, circleCrop = false)
                        audioAttachment.visibility = View.GONE
                    }
                    if (it.type == AttachmentType.AUDIO) {
                        audioAttachment.visibility = View.VISIBLE
                        audioTrackName.text = it.url.substringAfterLast('/')
                    }
                }
                if (post.attachment == null){
                    audioAttachment.visibility = View.GONE
                    imageAttachment.visibility = View.GONE
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

                playPauseAudio.setOnClickListener {
                    mediaObserver.player?.let{
                        var url = post.attachment!!.url
                        if (mediaObserver.currentUrl.isNotBlank() && mediaObserver.currentUrl != url) {
                            mediaObserver.onStop()
                        }
                        if (mediaObserver.player == null || mediaObserver.player?.isPlaying!!) {
                            mediaObserver?.onPause()
                        } else {
                            if (mediaObserver.currentUrl != url) {
                                mediaObserver.apply {
                                    player?.setDataSource(url)
                                    player?.prepare()
                                    currentUrl = url
                                    trackButton = playPauseAudio
                                }.onPlay()
                            } else
                                mediaObserver.onPlay()
                        }
//                        if (it.isPlaying)
//                            mediaObserver.onPause()
//                        else {
//                            if (mediaObserver.currentUrl != post.attachment!!.url)
//                            mediaObserver.onPlay()
//                        }
                    }
//                    if (mediaObserver.player != null) {
//                        var a = mediaObserver
//                        if (mediaObserver.player!!.isPlaying) {
//                            mediaObserver.onPause()
//                        } else {
//                            mediaObserver.onPlay()
//                        }
//                    }
                }
            }

        }
    }
}