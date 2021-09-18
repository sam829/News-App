package com.example.inshortclone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inshortclone.databinding.NewsItemBinding

class NewsAdapter(private val listener: NewsItemSwiped) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val newsList: ArrayList<News> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        /*val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)*/
        val binder = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = NewsViewHolder(binder)
        binder.root.setOnTouchListener(object : OnSwipeTouchListener(parent.context) {

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                listener.onNewsSwipeLeft(newsList[viewHolder.adapterPosition])
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                listener.onNewsSwipeRight(newsList[viewHolder.adapterPosition])
            }

        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        with(holder) {
            with(newsList[position]) {
                binder.newsTitle.text = this.title
                binder.newsContent.text = this.content
                binder.newsAuthor.text = this.author

                Glide.with(itemView.context).load(this.imageUrl).into(binder.newsImage)
            }
        }
    }

    override fun getItemCount() = newsList.size

    inner class NewsViewHolder(val binder: NewsItemBinding) : RecyclerView.ViewHolder(binder.root)

    fun updateNews(updatedNews: ArrayList<News>) {
        newsList.clear()
        newsList.addAll(updatedNews)

        notifyDataSetChanged()
    }
}

interface NewsItemSwiped {
    fun onNewsSwipeLeft(news: News)
    fun onNewsSwipeRight(news: News)
}