package com.example.solidarapp.account

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.solidarapp.R
import com.example.solidarapp.password_recover.ChangePasswordActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.json.JSONObject

class EditMyAccountActivity : AppCompatActivity() {

    private lateinit var url : String

    private lateinit var storageRef: StorageReference
    private lateinit var uri: Uri
    private var downloadUrl: String? = "https://i.imgur.com/bcQL91M.jpg"
    private var changeProfilePictureTrigger : Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_my_account_layout)

        storageRef = FirebaseStorage.getInstance().getReference("images")

        supportActionBar?.setTitle("Actualizar Datos de Mi Cuenta")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        val intent = intent
        val userCurrentName = intent.getStringExtra("userName")
        val userCurrentProfilePicture = intent.getStringExtra("userProfilePicture")
        val userCurrentEmail = intent.getStringExtra("userEmail")

        val etNewUserName = findViewById<EditText>(R.id.etNewUserName)
        val ivUserProfilePicture = findViewById<ImageView>(R.id.ivUserProfilePictureEdit)
        val etNewUserEmail = findViewById<EditText>(R.id.etNewEmail)

        etNewUserName.setText(userCurrentName)
        Glide.with(this).load(userCurrentProfilePicture).centerCrop().transform(CropCircleTransformation()).into(ivUserProfilePicture)
        etNewUserEmail.setText(userCurrentEmail)

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                ivUserProfilePicture.setImageURI(it)
                uri = it!!
            }
        )

        val btChangeProfilePicture = findViewById<ImageButton>(R.id.ibChangeProfilePicture)
        btChangeProfilePicture.setOnClickListener{
            changeProfilePictureTrigger = true
            galleryImage.launch("image/*")
        }

        val btSaveChanges = findViewById<Button>(R.id.btSaveChanges)
        btSaveChanges.setOnClickListener{

            val newUserEmail = etNewUserEmail.text.toString()
            val newUserName = etNewUserName.text.toString()
            val lastUserEmail = userCurrentEmail.toString()

            updateAccountInfo(newUserEmail, newUserName, lastUserEmail, changeProfilePictureTrigger)

            if (changeProfilePictureTrigger == true){

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

                        updateProfilePicture(newUserEmail, downloadUrl.toString())
                    } else {
                        // Handle error
                    }
                }

            }

        }

        val btChangePassword = findViewById<Button>(R.id.btChangePassword)
        btChangePassword.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("userRecoveryEmail", userCurrentEmail)
            startActivity(intent)
        }

    }

    private fun updateAccountInfo(userEmail: String, userName: String, lastUserEmail : String, pictureTrigger : Boolean){
        url = getString(R.string.API_URL) + "updateAccountInfo.php"

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

                        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userEmail", userEmail)
                        editor.apply()

                        if(changeProfilePictureTrigger == false){
                            val intent = Intent(this, MyAccountActivity::class.java)
                            startActivity(intent)
                        }

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
                params["userName"] = userName
                params["lastUserEmail"] = lastUserEmail
                params["pictureTrigger"] = pictureTrigger.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun updateProfilePicture(userEmail: String, userProfilePicture : String){
        url = getString(R.string.API_URL) + "updateProfilePicture.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    println(response.toString())
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        val intent = Intent(this, MyAccountActivity::class.java)
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
                params["userEmail"] = userEmail
                params["userProfilePicture"] = userProfilePicture
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
}