package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.Constants
import ru.netology.nmedia.util.PostDealtWith
import ru.netology.nmedia.viewModel.PostViewModel

class MainFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment   //предоставляем viemodel нескольким фрагментам
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun like(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun share(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(
                        intent,
                        getString(R.string.chooser_share_post)
                    ) //создается chooser - выбор между приложениями
                startActivity(shareIntent)

                viewModel.shareById(post.id)
            }

            override fun view(post: Post) {
                viewModel.viewById(post.id)
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                PostDealtWith.savePostDealtWith(post)
                findNavController().navigate(R.id.action_mainFragment_to_editPostFragment)
            }

            override fun play(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink))
                val packageManager = requireActivity().packageManager
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            override fun showPost(post: Post) {
                PostDealtWith.savePostDealtWith(post)
                findNavController().navigate(R.id.action_mainFragment_to_cardPostFragment)
            }

            override fun addLink(id: Long, link: String) {
                viewModel.addLink(id, link)
            }

            override fun retry(content: String) {
                viewModel.changeContentAndSave(content)
            }
        }
        )

        binding.recyclerList.adapter = adapter  // получаю доступ к RecyclerView

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val newPost =
                feedModel.posts.size > adapter.currentList.size //проверяем, что это добавление поста, а не др действие (лайк и т.п.)

            adapter.submitList(feedModel.posts) {
                if (newPost) {
                    binding.recyclerList.smoothScrollToPosition(0)
                } // scroll к верхнему сообщению только при добавлении
            }
            binding.emptyText.isVisible = feedModel.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            binding.progress.isVisible = feedModelState.loading
            binding.errorGroup.isVisible = feedModelState.error
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            //viewModel.loadPosts()
            binding.swiperefresh.isRefreshing = false
        }

        binding.addPost.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_newPostFragment,
                // Bundle().apply { textArg = viewModel.draft }             // черновик с помощью VieModel
                Bundle().apply {
                    val text = (activity as AppActivity).getSharedPreferences(
                        Constants.DRAFT_PREF_NAME,
                        Context.MODE_PRIVATE
                    ).getString(Constants.DRAFT_KEY, "")
                    textArg = text
                }
            )
        }

        return binding.root
    }
}