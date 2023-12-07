package com.example.solidarapp.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.solidarapp.FeedActivity
import com.example.solidarapp.InitialActivity
import com.example.solidarapp.R
import com.example.solidarapp.posts.MakePostActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import jp.wasabeef.glide.transformations.CropCircleTransformation

class MyAccountActivity : AppCompatActivity() {

    private lateinit var url: String

    private var userName : String = ""
    private var userEmail : String = ""
    private var userAccountType : String = ""
    private var userProfilePicture: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_account_layout)

        supportActionBar?.setTitle("SolidarApp | Mi Cuenta")
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

        // Acceder al userEmail almacenado al Iniciar Sesión.
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "").toString()

        getAccountInfo(userEmail.toString())

        val btLogOut = findViewById<Button>(R.id.btLogOut)

        btLogOut.setOnClickListener{
            sharedPreferences.edit().remove("userEmail").apply()

            val intent = Intent(this, InitialActivity::class.java)
            startActivity(intent)
        }

        navigation.selectedItemId = R.id.btnMenuMyAccount
    }

    private fun getAccountInfo(userEmail: String){
        url = getString(R.string.API_URL) + "getAccountInfo.php"

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

                        val tvUserName = findViewById<TextView>(R.id.tvUserName)
                        userName = userObject.getString("username")
                        tvUserName.text = userName

                        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
                        tvUserEmail.text = userEmail

                        val tvUserAccountType = findViewById<TextView>(R.id.tvUserAccountType)
                        userAccountType = userObject.getString("account_type")
                        tvUserAccountType.text = userAccountType

                        val tvUserRegistrationDate = findViewById<TextView>(R.id.tvUserRegistrationDate)
                        val userRegistrationDate = userObject.getString("register_date")
                        tvUserRegistrationDate.text = userRegistrationDate

                        val ivUserProfilePicture = findViewById<ImageView>(R.id.ivUserProfilePicture)
                        userProfilePicture = userObject.getString("picture")
                        Glide.with(this).load(userProfilePicture).centerCrop().transform(CropCircleTransformation()).into(ivUserProfilePicture)

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

        when (item.itemId) {

            R.id.editItem -> {

                val intent = Intent(this, EditMyAccountActivity::class.java)
                intent.putExtra("userName", userName)
                intent.putExtra("userEmail", userEmail)
                intent.putExtra("userAccountType", userAccountType)
                intent.putExtra("userProfilePicture", userProfilePicture)
                startActivity(intent)
                return true

            } else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun isCurrentActivity(cls: Class<*>): Boolean {
        return javaClass.name == cls.name
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.myaccount_menu, menu)
        return true
    }
}