package com.example.inshortclone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.inshortclone.databinding.CategoryItemBinding

class CategoryAdapter(
    private val context: Context,
    private val categories: ArrayList<Category>,
    private val listener: CategoryItemClicked
) :
    BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
//        val itemView = LayoutInflater.from(context).inflate(R.layout.category_item, p2, false)

        val binder = CategoryItemBinding.inflate(LayoutInflater.from(context), p2, false)
        binder.root.setOnClickListener {
            listener.onItemClicked(categories[p0].title)
        }
        val categoryTitle: TextView = binder.categoryTitle
        val categoryImage: ImageView = binder.categoryImage

        categoryTitle.text = categories[p0].title
        val imageId = when(categories[p0].imageName) {
            "live_news" -> R.drawable.live_news
            "media" -> R.drawable.media
            "newspaper" -> R.drawable.newspaper
            "podcast" -> R.drawable.podcast
            "envelope" -> R.drawable.envelope
            else -> R.drawable.newspaper
        }
        categoryImage.setImageDrawable(ContextCompat.getDrawable(context, imageId))

        return binder.root
    }

    override fun getCount() = categories.size

    override fun getItem(p0: Int) = categories[p0]

    override fun getItemId(p0: Int) = p0.toLong()
}

interface CategoryItemClicked {
    fun onItemClicked(categoryTitle: String)
}