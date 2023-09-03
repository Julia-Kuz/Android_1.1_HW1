package ru.netology.nmedia.adapter

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.numberRepresentation

interface OnInteractionListener {
    fun like(post: Post)
    fun share(post: Post)
    fun view(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun play(post: Post)
    fun showPost(post: Post)
    fun addLink(id: Long, link: String)
}

class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            FragmentCardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            content.movementMethod = ScrollingMovementMethod()
            likesIcon.isChecked = post.likedByMe
            likesIcon.text = numberRepresentation(post.likes)
            shareIcon.text = numberRepresentation(post.share)
            viewIcon.text = numberRepresentation(post.views)
            groupLink.visibility = View.GONE

            if (post.videoLink != null) {
                groupPlay.visibility = View.VISIBLE
            } else groupPlay.visibility = View.GONE

            likesIcon.setOnClickListener {
                onInteractionListener.like(post)
            }
            shareIcon.setOnClickListener {
                onInteractionListener.share(post)
            }
            viewIcon.setOnClickListener {
                onInteractionListener.view(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_optons)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()           // ПОМНИТЬ прo show()!!!
            }
            play.setOnClickListener {
                onInteractionListener.play(post)
            }
            videoLink.setOnClickListener {
                onInteractionListener.play(post)
            }
            content.setOnClickListener {
                onInteractionListener.showPost(post)
            }

            linkIcon.setOnClickListener {
                groupLink.visibility = View.VISIBLE
                linkSave.setOnClickListener {
                    if (videoLinkText.text.toString().isNotBlank()) {
                        onInteractionListener.addLink(post.id, videoLinkText.text.toString())
                    } else groupLink.visibility = View.GONE
                }
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
