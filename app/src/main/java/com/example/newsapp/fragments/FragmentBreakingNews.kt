package com.example.newsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.ArticleAdapter
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.ui.ArticleFactory
import com.example.newsapp.util.ArticleApplication

class FragmentBreakingNews : Fragment() {
    private var fragmentBreakingNewsBinding: FragmentBreakingNewsBinding? = null
    private val binding get() = fragmentBreakingNewsBinding!!
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBreakingNewsBinding = FragmentBreakingNewsBinding
            .inflate(inflater, container, false)
        val view = binding.root
        setViewModel()
        setRecyclerView(articleViewModel)
        observeArticlesListToShowInRecyclerView()
        observeProgressToShowOrChangeVisibility()
        return view
    }

    private fun setViewModel() {
        articleViewModel = ViewModelProvider(
            requireActivity(), ArticleFactory(
                requireActivity().application,
                (requireActivity().application as ArticleApplication).articleRepository
            )
        ).get(ArticleViewModel::class.java)
    }

    private fun observeProgressToShowOrChangeVisibility() {
        articleViewModel.displayedProgressBarBreakingNews.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == 1) {
                    fragmentBreakingNewsBinding?.progressBarBreakingNews?.visibility = View.VISIBLE
                } else {
                    fragmentBreakingNewsBinding?.progressBarBreakingNews?.visibility = View.GONE
                }
            }
        })
    }

    private fun observeArticlesListToShowInRecyclerView() {
        articleViewModel.headlines.observe(viewLifecycleOwner, {
            if (it != null) {
                articleAdapter.getAsyncListDiffer().submitList(it.toList())
            }
        })
    }

    private fun setRecyclerView(articleViewModel: ArticleViewModel) {
        with(binding.recyclerViewHeadlines) {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            articleAdapter = ArticleAdapter()
            adapter = articleAdapter
            setOnScrollListenerForRecyclerView()

            articleAdapter.onItemClick = { article ->
                articleViewModel.articleToShowInWebView.postValue(article)
                findNavController().navigate(R.id.action_fragmentBreakingNews_to_fragmentWebView)
            }
        }
    }

    private fun setOnScrollListenerForRecyclerView() {
        fragmentBreakingNewsBinding?.recyclerViewHeadlines?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val listOfArticles = articleViewModel.headlines.value
                    val lastCompletelyVisibleItemPosition =
                        linearLayoutManager?.findLastCompletelyVisibleItemPosition()

                    if (linearLayoutManager != null &&
                        listOfArticles != null &&
                        lastCompletelyVisibleItemPosition == listOfArticles.size - 1
                    ) {
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadMore() {
        articleViewModel.getHeadlines()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBreakingNewsBinding = null
    }
}