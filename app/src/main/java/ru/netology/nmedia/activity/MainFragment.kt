package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.Constants
import ru.netology.nmedia.util.PostDealtWith
import ru.netology.nmedia.viewModel.AuthViewModel
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.SignInViewModel

class MainFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment   //предоставляем viemodel нескольким фрагментам
    )
    private val viewModelAuth: AuthViewModel by viewModels()


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
                if (viewModelAuth.authenticated) {
                    viewModel.likeById(post.id)
                } else {
                    findNavController().navigate(R.id.action_mainFragment_to_dialogFragment)
                }
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

            override fun showPhoto(post: Post) {
                PostDealtWith.savePostDealtWith(post)
                findNavController().navigate(R.id.action_mainFragment_to_photoFragment2)
            }
            override fun addLink(id: Long, link: String) {
                viewModel.addLink(id, link)
            }

            override fun retryPost(content: String) {
                viewModel.changeContentAndSave(content)
            }
        }
        )

        binding.newPosts.visibility = View.GONE

        //реализация меню для авторизации с ToolBar

        val toolbar: Toolbar = binding.toolbarMain

        // с использованием Live data
//        viewModelAuth.dataAuth.observe(viewLifecycleOwner) {
//            toolbar.invalidateMenu()
//        }

        // с использованием Flow
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED){ //наблюдаем за авторизацией, только когда активити/фрагмент доступен для взаимодействия
                viewModelAuth.dataAuth.collect{
                    toolbar.invalidateMenu()
                }
            }
        }

        toolbar.addMenuProvider (object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModelAuth.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModelAuth.authenticated)
                }
            }

//            override fun onPrepareMenu(menu: Menu) {
//                menu.setGroupVisible(R.id.unauthenticated, !viewModelAuth.authenticated)
//                menu.setGroupVisible(R.id.authenticated, viewModelAuth.authenticated)
//            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
                        true
                    }

                    R.id.signup -> {
                        findNavController().navigate(R.id.action_mainFragment_to_signUpFragment)
                        true
                    }

                    R.id.signout -> {
                        findNavController().navigate(R.id.action_mainFragment_to_dialogFragmentSignOut)
                        true
                    }

                    else -> false
                }
        })

        binding.recyclerList.adapter = adapter  // получаю доступ к RecyclerView

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->

//            val newPost =
//                feedModel.posts.size > adapter.currentList.size //проверяем, что это добавление поста, а не др действие (лайк и т.п.)
//            adapter.submitList(feedModel.posts) {
//                if (newPost) {
//                    binding.recyclerList.smoothScrollToPosition(0)
//                } // scroll к верхнему сообщению только при добавлении
//            }

            val visiblePosts = feedModel.posts.filter { !it.hidden }
            val difference = visiblePosts.size > adapter.currentList.size
            val recyclerNotEmpty = adapter.currentList.size != 0 //чтобы при возвращении с фрагмента с фото, лента оставалась на посте с этим фото

            adapter.submitList(visiblePosts) {
                if (difference && recyclerNotEmpty) {
                    binding.recyclerList.smoothScrollToPosition(0)
                    binding.newPosts.visibility = View.GONE
                }
            }
            binding.emptyText.isVisible = feedModel.empty
        }

        var count = 0
        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                count++
                if (count > 1) {binding.newPosts.text = "$count New Posts"}
                else {binding.newPosts.text = "$count New Post"}
                binding.newPosts.visibility = View.VISIBLE
            }
        }

        binding.newPosts.setOnClickListener {
            viewModel.updatePosts()
            binding.newPosts.visibility = View.GONE
            binding.recyclerList.smoothScrollToPosition(0)
            count = 0
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
            if (viewModelAuth.authenticated) {
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
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_dialogFragment)
            }

        }

        return binding.root
    }
}