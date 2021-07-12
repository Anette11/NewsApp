package com.example.newsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.ArticleAdapter
import com.example.newsapp.databinding.FragmentBookmarksBinding
import com.example.newsapp.ui.ArticleFactory
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.util.ArticleApplication
import com.google.android.material.snackbar.Snackbar

class FragmentBookmarks : Fragment() {
    private var fragmentBookmarksBinding: FragmentBookmarksBinding? = null
    private val binding get() = fragmentBookmarksBinding!!
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookmarksBinding = FragmentBookmarksBinding
            .inflate(inflater, container, false)
        val view = binding.root
        setViewModel()
        observeArticlesToShowInRecyclerView()
        setRecyclerView(articleViewModel)
        setSwipeToDelete(view)
        return view
    }

    private fun observeArticlesToShowInRecyclerView() {
        articleViewModel.allSavedArticles.observe(viewLifecycleOwner, { listOfArticles ->
            articleAdapter.getAsyncListDiffer().submitList(listOfArticles)
        })
    }

    private fun setViewModel() {
        articleViewModel = ViewModelProvider(
            requireActivity(), ArticleFactory(
                requireActivity().application,
                (requireActivity().application as ArticleApplication).articleRepository
            )
        ).get(ArticleViewModel::class.java)
    }

    private fun setSwipeToDelete(view: View) {
        val itemTouchHelperSimpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val article = articleAdapter.getAsyncListDiffer().currentList[position]
                articleViewModel.deleteArticle(article)

                Snackbar.make(view, getString(R.string.deleted), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) { articleViewModel.saveArticle(article) }
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperSimpleCallback)
            .attachToRecyclerView(fragmentBookmarksBinding?.recyclerViewBookmarks)
    }

    private fun setRecyclerView(articleViewModel: ArticleViewModel) {
        with(binding.recyclerViewBookmarks) {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            articleAdapter = ArticleAdapter()
            adapter = articleAdapter

            articleAdapter.onItemClick = { article ->
                articleViewModel.articleToShowInWebView.postValue(article)
                findNavController().navigate(R.id.action_fragmentBookmarks_to_fragmentWebView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBookmarksBinding = null
    }
}