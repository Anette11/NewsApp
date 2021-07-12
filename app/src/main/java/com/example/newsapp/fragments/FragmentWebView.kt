package com.example.newsapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentWebviewBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.ArticleFactory
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.util.ArticleApplication
import com.google.android.material.snackbar.Snackbar

class FragmentWebView : Fragment() {
    private var fragmentWebViewBinding: FragmentWebviewBinding? = null
    private val binding get() = fragmentWebViewBinding!!
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWebViewBinding = FragmentWebviewBinding
            .inflate(inflater, container, false)
        val view = binding.root
        setViewModel()
        observeArticleToShowInWebView()
        observeProgressToShowOrChangeVisibility()
        setFloatingActionButton(view)
        return view
    }

    private fun observeProgressToShowOrChangeVisibility() {
        articleViewModel.displayedProgressBarWebView.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == 1) {
                    fragmentWebViewBinding?.progressBarWebView?.visibility = View.VISIBLE
                } else {
                    fragmentWebViewBinding?.progressBarWebView?.visibility = View.GONE
                }
            }
        })
    }

    private fun observeArticleToShowInWebView() {
        articleViewModel.articleToShowInWebView.observe(viewLifecycleOwner, { article ->
            if (article != null) {
                loadWebView(article)
            }
        })
    }

    private fun loadWebView(article: Article) {
        article.url?.let {
            fragmentWebViewBinding?.webView?.apply {

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        articleViewModel.displayedProgressBarWebView.postValue(1)
                    }

                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        articleViewModel.displayedProgressBarWebView.postValue(0)
                    }
                }
                loadUrl(it)
            }
        }
    }

    private fun setViewModel() {
        articleViewModel = ViewModelProvider(
            requireActivity(), ArticleFactory(
                requireActivity().application,
                (requireActivity().application as ArticleApplication).articleRepository
            )
        ).get(ArticleViewModel::class.java)
    }

    private fun setFloatingActionButton(view: View) {
        fragmentWebViewBinding?.floatingActionButton?.setOnClickListener {
            val article = articleViewModel.articleToShowInWebView.value
            if (article != null) {
                articleViewModel.saveArticle(article)
                Snackbar.make(view, getString(R.string.saved), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentWebViewBinding = null
    }
}