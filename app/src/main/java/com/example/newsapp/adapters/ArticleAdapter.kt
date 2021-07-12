package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.OneItemBinding
import com.example.newsapp.models.Article

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(private val oneItemBinding: OneItemBinding) :
        RecyclerView.ViewHolder(oneItemBinding.root) {
        fun bind(article: Article) = with(oneItemBinding) {
            textViewTitle.text = article.title
            textViewDescription.text = article.description
            textViewTime.text = article
                .publishedAt?.replace("T", " ")?.replace("Z", "")
            textViewSource.text = article.source?.name

            Glide.with(itemView)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val oneItemBinding: OneItemBinding =
            OneItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(oneItemBinding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = asyncListDiffer.currentList[position]
        holder.bind(article)
        holder.itemView.setOnClickListener { onItemClick?.invoke(article) }
    }

    private val diffUtilItemCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem.url == newItem.url
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtilItemCallback)

    fun getAsyncListDiffer() = asyncListDiffer

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    var onItemClick: ((Article) -> Unit)? = null
}