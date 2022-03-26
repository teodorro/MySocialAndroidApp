package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.EventItemBinding
import com.example.mysocialandroidapp.dto.Event
import com.example.mysocialandroidapp.enumeration.UserListType


interface OnEventInteractionListener {
    fun onLike(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onShowUsers(event: Event, userListType: UserListType){}
}

class EventsAdapter (
    private val onInteractionListener: OnEventInteractionListener,
    private val userId: Long
) : ListAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        var binding =
            EventItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return EventViewHolder(binding, onInteractionListener, userId)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }



    class EventViewHolder(
        private val binding: EventItemBinding,
        private val onInteractionListener: OnEventInteractionListener,
        private val userId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                author.text = event.author
                content.text = event.content
                like.isChecked = event.likedByMe
                like.text = "${event.likeOwnerIds.size}"
                published.text = event.published
                dateTime.text = event.datetime
                speakers.text = "${event.speakerIds.size}"
                participants.text = "${event.participantsIds.size}"
                participants.isChecked = event.participatedByMe
                link.text = event.link
                if (event.link.isNullOrBlank()) {
                    link.visibility = View.INVISIBLE
                }
                if (event.coords != null){
                    coords.text = "${event.coords!!.lat}° N, ${event.coords!!.long}° E"
                } else{
                    coords.visibility = View.INVISIBLE
                }
                if (attachment != null){
                    //TODO set attachment source
                }
                menu.visibility = View.VISIBLE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_event)
                        menu.setGroupVisible(R.id.owned, userId == event.authorId)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(event)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(event)
                                    true
                                }
                                R.id.likes -> {
                                    onInteractionListener.onShowUsers(event, UserListType.LIKES)
                                    true
                                }
                                R.id.participants -> {
                                    onInteractionListener.onShowUsers(event, UserListType.PARTICIPANTS)
                                    true
                                }
                                R.id.speakers -> {
                                    onInteractionListener.onShowUsers(event, UserListType.SPEAKERS)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(event)
                    like.isChecked = !like.isChecked
                }
            }

        }
    }
}