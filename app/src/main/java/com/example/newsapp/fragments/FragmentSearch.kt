package com.example.newsapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.ui.ArticleFactory
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.util.ArticleApplication

class FragmentSearch : Fragment() {
    private var fragmentSearchBinding: FragmentSearchBinding? = null
    private val binding get() = fragmentSearchBinding!!
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSearchBinding = FragmentSearchBinding
            .inflate(inflater, container, false)
        val view = binding.root
        setViewModel()
        observeArticlesToShowInRecyclerView()
        observeProgressToShowOrChangeVisibility()
        addTextChangedListenerForEditText()
        setRecyclerView()
        return view
    }

    private fun addTextChangedListenerForEditText() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val searchKeyword = editable.toString()
                if (searchKeyword.isNotEmpty()) {
                    articleViewModel.editTextSearchKeyword.postValue(searchKeyword)
                    articleViewModel.searchByKeyword(searchKeyword)
                }
            }
        })
    }

    private fun observeProgressToShowOrChangeVisibility() {
        articleViewModel.displayedProgressBarSearch.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == 1) {
                    fragmentSearchBinding?.progressBarSearch?.visibility = View.VISIBLE
                } else {
                    fragmentSearchBinding?.progressBarSearch?.visibility = View.GONE
                }
            }
        })
    }

    private fun observeArticlesToShowInRecyclerView() {
        articleViewModel.newsSearched.observe(viewLifecycleOwner, {
            if (it != null) {
                articleAdapter.getAsyncListDiffer().submitList(it.toList())
            }
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

    private fun setRecyclerView() {
        with(binding.recyclerViewSearch) {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            articleAdapter = ArticleAdapter()
            adapter = articleAdapter
            setOnScrollListenerForRecyclerView()

            articleAdapter.onItemClick = { article ->
                articleViewModel.articleToShowInWebView.postValue(article)
                findNavController().navigate(R.id.action_fragmentSearch_to_fragmentWebView)
            }
        }
    }

    private fun setOnScrollListenerForRecyclerView() {
        fragmentSearchBinding?.recyclerViewSearch?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val listOfArticles = articleViewModel.newsSearched.value
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
        val keyWord = articleViewModel.editTextSearchKeyword.value.toString()
        if (keyWord.isNotEmpty()) {
            articleViewModel.searchByKeywordLoadMore(keyWord)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSearchBinding = null
    }
}