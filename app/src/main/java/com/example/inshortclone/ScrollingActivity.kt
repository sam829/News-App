package com.example.inshortclone

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.toolbox.JsonObjectRequest
import com.example.inshortclone.databinding.ActivityScrollingBinding
import kotlin.math.abs
import kotlin.math.max

class ScrollingActivity : AppCompatActivity(), NewsItemSwiped, CategoryItemClicked {

    private lateinit var binder: ActivityScrollingBinding
    private lateinit var mNewsAdapter: NewsAdapter
    private lateinit var mCategoryRequested: String
    private lateinit var url: String
    private lateinit var categoryView: View
    private lateinit var mainLayoutView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binder.root)
        mCategoryRequested = null.toString()

        //User you newsorg api key is place of API_KEY
        url =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=API_KEY"

        fetchData()
        mNewsAdapter = NewsAdapter(this)
        binder.newsViewPager.adapter = mNewsAdapter
        binder.newsViewPager.setPageTransformer { page, position ->
            val minScale = 0.85f
            val minAlpha = 0.5f
            page.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = max(minScale, 1 - abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (minAlpha +
                                (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }


        categoryView = binder.categoriesView.root
        mainLayoutView = binder.newsViewPager
        binder.categoriesView.gridView.adapter = CategoryAdapter(this, setCategories(), this)
        getURL(mCategoryRequested)
    }

    private fun fetchData() {
        val newsList: ArrayList<News> = ArrayList()
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            {
                val newsJsonArray = it.getJSONArray("articles")
                for (index in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(index)

                    newsList.add(
                        News(
                            newsJsonObject.getString("title"),
                            newsJsonObject.getString("content"),
                            newsJsonObject.getString("url"),
                            newsJsonObject.getString("urlToImage"),
                            newsJsonObject.getString("author")
                        )
                    )
                }
                mNewsAdapter.updateNews(newsList)
            },
            {
                Log.e("Error Data from API", it.localizedMessage ?: "Successfully fetched")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        VolleyRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    override fun onNewsSwipeLeft(news: News) {
        CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(news.url))
    }

    override fun onNewsSwipeRight(news: News) {
        mainLayoutView.visibility = View.GONE
        categoryView.visibility = View.VISIBLE
    }

    private fun setCategories(): ArrayList<Category> {
        val categories = ArrayList<Category>()
        categories.add(Category("Business", "live_news"))
        categories.add(Category("Entertainment", "media"))
        categories.add(Category("General", "newspaper"))
        categories.add(Category("Health", "podcast"))
        categories.add(Category("Science", "envelope"))
        categories.add(Category("Sports", "newspaper"))
        categories.add(Category("Technology", "live_news"))
        return categories
    }

    override fun onItemClicked(categoryTitle: String) {
        mCategoryRequested = categoryTitle
        url = getURL(categoryTitle)
        fetchData()
        categoryView.visibility = View.GONE
        mainLayoutView.visibility = View.VISIBLE
    }

    private fun getURL(category: String): String {
        when (category) {
            "Business" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=API_KEY"
            "Entertainment" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=entertainment&apiKey=API_KEY"
            "General" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=general&apiKey=API_KEY"
            "Health" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=health&apiKey=API_KEY"
            "Science" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=science&apiKey=API_KEY"
            "Sports" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=API_KEY"
            "Technology" -> url =
                "https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=API_KEY"
            else -> url =
                "https://newsapi.org/v2/top-headlines?country=in&apiKey=API_KEY"
        }
        return url
    }
}