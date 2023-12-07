package com.example.solidarapp.posts
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.solidarapp.FeedActivity
import com.example.solidarapp.account.MyAccountActivity
import com.example.solidarapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.json.JSONObject

class MakePostActivity : AppCompatActivity() {

    private lateinit var url: String

    private lateinit var storageRef: StorageReference
    private lateinit var uri: Uri
    private var downloadUrl: String? = null

    private var userName : String = ""
    private var userEmail : String = ""
    private var userAccountType : String = ""
    private var userProfilePicture: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_post_layout)

        storageRef = FirebaseStorage.getInstance().getReference("images")

        supportActionBar?.setTitle("SolidarApp | Nueva Publicación")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.logo)

        val navigation: BottomNavigationView = findViewById(R.id.simple_bottom_navigation)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuFeed -> {
                    if (!isCurrentActivity(FeedActivity::class.java)) {
                        startActivity(Intent(this, FeedActivity::class.java))
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.btnMenuCreatePost -> {
                    if (!isCurrentActivity(MakePostActivity::class.java)) {
                        startActivity(Intent(this, MakePostActivity::class.java))
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.btnMenuMyAccount -> {
                    if (!isCurrentActivity(MyAccountActivity::class.java)) {
                        startActivity(Intent(this, MyAccountActivity::class.java))
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

        navigation.selectedItemId = R.id.btnMenuCreatePost

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "").toString()
        getAccountInfo(userEmail)

        val ivNewPostPicture = findViewById<ImageView>(R.id.ivNewPostPicture)
        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                ivNewPostPicture.setImageURI(it)
                uri = it!!
            }
        )

        val ibNewPostSearchPicture = findViewById<ImageButton>(R.id.ibNewPostSearchPicture)
        ibNewPostSearchPicture.setOnClickListener{
            galleryImage.launch("image/*")
            ivNewPostPicture .setVisibility(View.VISIBLE);
        }

        val ibNewPostPublish = findViewById<ImageButton>(R.id.ibNewPostPublish)
        val etNewPostDescription = findViewById<EditText>(R.id.etNewPostDescription)

        ibNewPostPublish.setOnClickListener{

            val imageRef = storageRef.child(System.currentTimeMillis().toString())
            val uploadTask = imageRef.putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadUrl = task.result.toString()
                    println(downloadUrl)

                    val newPostDescription = etNewPostDescription.text.toString()
                    val newPostPicture = downloadUrl.toString()

                    newPost(newPostDescription, newPostPicture, userEmail)

                } else {

                }
            }

        }

    }

    private fun newPost(newPostDescription: String, newPostPicture: String, newPostOwner: String){
        url = getString(R.string.API_URL)+"newPost.php"

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
                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
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
                params["newPostDescription"] = newPostDescription
                params["newPostPicture"] = newPostPicture
                params["newPostOwner"] = newPostOwner
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun getAccountInfo(userEmail: String){
        url = getString(R.string.API_URL)+"getAccountInfo.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response.toString())
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        val userObject = jsonResponse.getJSONObject("accountInformation")

                        val ivNewPostUserPicture = findViewById<ImageView>(R.id.ivNewPostUserPicture)
                        userProfilePicture = userObject.getString("picture")
                        Glide.with(this).load(userProfilePicture).centerCrop().transform(CropCircleTransformation()).into(ivNewPostUserPicture)

                        val newPostUserName = findViewById<TextView>(R.id.tvNewPostUserName)
                        userName = userObject.getString("username")
                        newPostUserName.text = userName

                        val newPostUserAccountType = findViewById<TextView>(R.id.tvNewPostUserAccountType)
                        userAccountType = userObject.getString("account_type")
                        newPostUserAccountType.text = userAccountType

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
                params["userEmail"] = userEmail
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun isCurrentActivity(cls: Class<*>): Boolean {
        return javaClass.name == cls.name
    }
}