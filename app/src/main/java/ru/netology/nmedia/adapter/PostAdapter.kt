package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.numberRepresentation

typealias OnLikeListener = (post: Post) -> Unit                   // что означает? как работает?

// вариант просто recyclerView (до "override fun onCreateViewHolder"):
/*class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnLikeListener,
    private val onViewListener: OnLikeListener
    ) : RecyclerView.Adapter<PostViewHolder>() {
    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
*/

// вариант DiffUtil
class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnLikeListener,
    private val onViewListener: OnLikeListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener, onViewListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        //val post = list[position]                                         // - просто recyclerView
        val post = getItem(position)                                        // - DiffUtil
        holder.bind(post)
    }
}

//    override fun getItemCount(): Int = list.size                          // - просто recyclerView
//}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnLikeListener,
    private val onViewListener: OnLikeListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesIcon.setImageResource(
                if (post.likedByMe) R.drawable.ic_like_red_24 else R.drawable.ic_like_24
            )
//            if (post.likedByMe) {
//                likesIcon.setImageResource(R.drawable.ic_like_red_24)        - распространенная ошибка (!)
//            }
            likesNumberText.text = numberRepresentation(post.likes)
            shareNumberText.text = numberRepresentation(post.share)
            viewNumberText.text = numberRepresentation(post.views)

            likesIcon.setOnClickListener {
                onLikeListener(post)                                      // еще разобраться!
            }
            shareIcon.setOnClickListener {
                onShareListener(post)
            }
            viewIcon.setOnClickListener {
                onViewListener(post)
            }
        }
    }
}


// создаем класс для DiffUtil (передаем в ListAdapter)

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}
