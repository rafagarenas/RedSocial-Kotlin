package com.example.solidarapp.posts.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.solidarapp.FeedActivity
import com.example.solidarapp.R
import org.json.JSONObject

class PostViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var url: String

    private val postUserPicture: ImageView = view.findViewById(R.id.ivPostUserPicture)
    private val postUserName: TextView = view.findViewById(R.id.tvPostUserName)
    private val postDate: TextView = view.findViewById(R.id.tvPostDate)
    private val postPicture: ImageView = view.findViewById(R.id.ivUserProfilePicture)
    private val postLikes: TextView = view.findViewById(R.id.tvPostLikes)
    private val postDescription: TextView = view.findViewById(R.id.tvPostDescription)
    private val btPostLike: ImageButton = view.findViewById(R.id.ibPostLike)
    private val btPostComment: ImageButton = view.findViewById(R.id.ibPostComments)
    private val btPostDelete : ImageButton = view.findViewById(R.id.ibPostDelete)

    private val sharedPreferences = view.context.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
    private var userEmail = sharedPreferences.getString("userEmail", "").toString()

    fun render(postModel: Post) {
        postUserPicture.loadImage(postModel.userProfilePicture)
        postPicture.loadImage(postModel.postPicture)

        postUserName.text = postModel.userPostName
        postDate.text = postModel.postDate

        if (postModel.postLikes == "Esta Publicación no tiene reacciones."){
            postLikes.visibility = View.GONE
        }else{
            postLikes.text = postModel.postLikes
            postLikes.visibility = View.VISIBLE
        }

        postDescription.text = postModel.postDescription

        btPostLike.setImageResource(R.drawable.baseline_whatshot_24)
        btPostDelete.visibility = View.GONE;

        btPostLike.setOnClickListener {
            if (!postModel.postLikedBy.contains(userEmail)){
                likePost(postModel.postID, userEmail)
            }else{
                removeLike(postModel.postID, userEmail)
                btPostLike.setImageResource(R.drawable.baseline_whatshot_24)
            }
        }

        btPostComment.setOnClickListener {
            showToast("Esta función no ha sido implementada. Espera futuras actualizaciones.")
        }

        if (userEmail == postModel.userPostEmail){
            btPostDelete.visibility = View.VISIBLE;
        }

        if (postModel.postLikedBy.contains(userEmail)){
            btPostLike.setImageResource(R.drawable.baseline_whatshot_liked_24)
        }

        btPostDelete.setOnClickListener{
            deletePost(postModel.postID)
        }

    }

    private fun likePost(postId : Int, likeAccount : String){
        url = view.context.getString(R.string.API_URL)+"likePost.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response.toString())
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast("$message")
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
                params["postId"] = postId.toString()
                params["likeAccount"] = likeAccount
                return params
            }
        }

        Volley.newRequestQueue(view.context).add(stringRequest)
    }

    private fun deletePost(postId : Int){
        url = view.context.getString(R.string.API_URL)+"deletePost.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response.toString())
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast("$message")
                        btPostDelete.visibility = View.INVISIBLE;
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
                params["postId"] = postId.toString()
                return params
            }
        }

        Volley.newRequestQueue(view.context).add(stringRequest)
    }

    private fun removeLike(postId : Int, likeAccount : String){
        url = view.context.getString(R.string.API_URL)+"removeLikeFromPost.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response.toString())
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast("$message")
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
                params["postId"] = postId.toString()
                params["likeAccount"] = likeAccount
                return params
            }
        }

        Volley.newRequestQueue(view.context).add(stringRequest)
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(context).load(url).centerCrop().into(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

}