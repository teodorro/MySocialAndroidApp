package com.example.mysocialandroidapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp.BuildConfig
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.api.TIMEOUT
import com.example.mysocialandroidapp.databinding.EventItemBinding
import com.example.mysocialandroidapp.dto.Event
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.util.DateStringFormatter
import com.example.mysocialandroidapp.util.loadCircleCrop


interface OnEventInteractionListener {
    fun onLike(event: Event) {}
    fun onParticipate(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onShowUsers(event: Event, userListType: UserListType) {}
}

class EventsAdapter(
    private val onInteractionListener: OnEventInteractionListener,
    private val userId: Long
//) : PagingDataAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {
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
        getItem(position)?.let {
            holder.bind(it)
        }
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
                if (event.authorAvatar != null) {
                    avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${event.authorAvatar}")
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_round)
                }
                content.text = event.content
                like.isChecked = event.likedByMe
                like.text = "${event.likeOwnerIds.size}"
                if (!event.published.isNullOrBlank())
                    published.text = DateStringFormatter.getDateTimeFromInstance(event.published)
                else
                    published.text = ""
                if (!event.datetime.isNullOrBlank())
                    date.text = DateStringFormatter.getDateFromInstance(event.datetime)
                else
                    date.text = ""
                if (!event.datetime.isNullOrBlank())
                    time.text = DateStringFormatter.getTimeFromInstance(event.datetime)
                else
                    time.text = ""
                speakers.text = "${event.speakerIds.size}"
                participate.text = "${event.participantsIds.size}"
                participate.isChecked = event.participatedByMe
                link.text = event.link
                if (event.link.isNullOrBlank()) {
                    link.visibility = View.GONE
                } else {
                    link.visibility = View.VISIBLE
                }
                if (event.coords != null) {
                    coords.text =
                        "http://www.google.com/maps/place/${event.coords!!.lat},${event.coords!!.long}"
                    coordsLine.visibility = View.VISIBLE
                } else {
                    coordsLine.visibility = View.GONE
                }
                event.attachment?.let {
                    if (it.type == AttachmentType.IMAGE) {
                        Glide.with(attachment)
                            .load(it.url)
                            .placeholder(R.drawable.ic_baseline_downloading_24)
                            .error(R.drawable.ic_baseline_error_24)
                            .timeout(TIMEOUT)
                            .into(attachment)
//                    imageViewAttachment.setOnClickListener {
//                        onInteractionListener.onShowPicAttachment(post)
//                    }
                    }
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
                                    onInteractionListener.onShowUsers(
                                        event,
                                        UserListType.PARTICIPANTS
                                    )
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

                participate.setOnClickListener {
                    onInteractionListener.onParticipate(event)
                    participate.isChecked = !participate.isChecked
                }

                speakers.setOnClickListener {
                    onInteractionListener.onShowUsers(event, UserListType.SPEAKERS)
                }
            }

        }
    }
}