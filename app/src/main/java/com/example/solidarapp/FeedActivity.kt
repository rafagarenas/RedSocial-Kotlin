package com.example.solidarapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.solidarapp.account.MyAccountActivity
import com.example.solidarapp.posts.MakePostActivity
import com.example.solidarapp.posts.adapter.Post
import com.example.solidarapp.posts.adapter.PostAdapter
import com.example.solidarapp.posts.adapter.PostProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

class FeedActivity : AppCompatActivity() {

    private lateinit var url: String
    private var filterTrigger: Boolean = false
    private val KEY_SUCCESS = "success"
    private val KEY_MESSAGE = "message"
    private val KEY_POSTS = "posts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed_layout)

        updateDataPeriodically()

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.logo)
        }

        initRecyclerView()

        val navigation: BottomNavigationView = findViewById(R.id.simple_bottom_navigation)
        navigation.setOnNavigationItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        requestPosts()
    }

    private fun handleNavigation(itemId: Int): Boolean {
        val targetActivity = when (itemId) {
            R.id.btnMenuFeed -> FeedActivity::class.java
            R.id.btnMenuCreatePost -> MakePostActivity::class.java
            R.id.btnMenuMyAccount -> MyAccountActivity::class.java
            else -> return false
        }

        if (!isCurrentActivity(targetActivity)) {
            startActivity(Intent(this, targetActivity))
        }

        return true
    }

    public fun requestPosts() {
        url = getString(R.string.API_URL) + "requestPosts.php"
        filterTrigger = false

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                handleResponse(response)
            },
            Response.ErrorListener { error ->
                handleErrorResponse(error)
            }) {
            override fun getParams(): Map<String, String> = HashMap()
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun handleResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val success = jsonResponse.getBoolean(KEY_SUCCESS)
            val message = jsonResponse.getString(KEY_MESSAGE)

            if (success) {
                val postsArray = jsonResponse.getJSONArray(KEY_POSTS)
                val postsList = parsePosts(postsArray)
                updateRecyclerView(postsList)
            } else {
                showToast(message)
            }
        } catch (e: Exception) {
            handleErrorResponse(e)
        }
    }

    private fun parsePosts(postsArray: JSONArray): List<Post> {
        val postsList = ArrayList<Post>()
        for (i in 0 until postsArray.length()) {
            val postObject = postsArray.getJSONObject(i)
            val post = Post(
                postObject.getInt("id"),
                postObject.getString("post_owner"),
                postObject.getString("post_user_profile_picture"),
                postObject.getString("post_user_name"),
                postObject.getString("post_date"),
                postObject.getString("post_picture"),
                postObject.getString("post_likes_count"),
                postObject.getString("post_description"),
                postObject.getString("post_liked_by")
            )
            postsList.add(post)
        }
        return postsList
    }

    private fun handleErrorResponse(error: Exception) {
        showToast("Error: ${error.message}")
        error.printStackTrace()
    }

    private fun updateRecyclerView(postsList: List<Post>) {
        val recyclerView = findViewById<RecyclerView>(R.id.feedRecyclerView)
        (recyclerView.adapter as? PostAdapter)?.updateData(postsList)
    }

    private fun isCurrentActivity(cls: Class<*>): Boolean {
        return javaClass.name == cls.name
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.feedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PostAdapter(PostProvider.PostList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private var filterType : String = ""

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filterSocialCenters -> {
                filterPosts("Social Center")
                filterType = "Social Center"
                showToast("Mostrando Publicaciones realizadas por Centros Sociales.")
                return true
            }
            R.id.filterAltruistDonators -> {
                filterPosts("Altruist Donator")
                filterType = ("Altruist Donator")
                showToast("Mostrando Publicaciones realizadas por Donadores Altruistas.")
                return true
            }
            R.id.filterAll -> {
                requestPosts()
                showToast("Mostrando Publicaciones sin filtros.")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun filterPosts(filterBy : String){
        url = getString(R.string.API_URL) + "filterPosts.php"
        filterTrigger = true

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response)
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        val postsArray = jsonResponse.getJSONArray(KEY_POSTS)
                        val postsList = parsePosts(postsArray)
                        updateRecyclerView(postsList)
                    } else {
                        showToast("$message")
                    }
                } catch (e: Exception) {
                    showToast("Error al procesar la Respuesta del Servidor: " +e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                println("Error: ${error.message}")
                showToast("${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["filterBy"] = filterBy
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun updateDataPeriodically() {
        val timer = Timer()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(!filterTrigger) {
                    requestPosts()
                }else if(filterType == "Social Center"){
                    filterPosts("Social Center")
                } else if(filterType == "Altruist Donator"){
                    filterPosts("Altruist Donator")
                }
            }
        }, 0, 1000)
    }
}
