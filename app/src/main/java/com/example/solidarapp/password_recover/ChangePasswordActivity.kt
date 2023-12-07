package com.example.solidarapp.password_recover

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.solidarapp.FeedActivity
import com.example.solidarapp.R
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var url : String

    private val etNewPassword by lazy { findViewById<EditText>(R.id.etNewPassword) }
    private val etConfirmNewPassword by lazy { findViewById<EditText>(R.id.etConfirmNewPassword) }
    private val btConfirmPasswordChange by lazy { findViewById<Button>(R.id.btConfirmPasswordChange) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password_layout)

        url = getString(R.string.API_URL) + "cambiarContraseña.php"

        supportActionBar?.title = "Cambiar Contraseña"

        val userRecoveryEmail = intent.getStringExtra("userRecoveryEmail").toString()

        btConfirmPasswordChange.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            val newConfirmedPassword = etConfirmNewPassword.text.toString()

            cambiarContraseña(userRecoveryEmail, newPassword, newConfirmedPassword)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun cambiarContraseña(userRecoveryEmail: String, newPassword: String, newConfirmedPassword: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast(message)

                        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        sharedPreferences.edit().putString("userEmail", userRecoveryEmail).apply()

                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
                    } else {
                        showToast(message)
                    }
                } catch (e: Exception) {
                    showToast("Error al procesar la Respuesta del Servidor: ${e.message}")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                showToast("Error: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "userRecoveryEmail" to userRecoveryEmail,
                    "newPassword" to newPassword,
                    "newConfirmedPassword" to newConfirmedPassword
                )
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
