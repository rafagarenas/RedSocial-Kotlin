package com.example.solidarapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.solidarapp.password_recover.ResetPasswordActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var url: String

    private val etEmail by lazy { findViewById<EditText>(R.id.etEmail) }
    private val etPassword by lazy { findViewById<EditText>(R.id.etPassword) }
    private val btLogin by lazy { findViewById<Button>(R.id.btLogin) }
    private val tvForgotPassword by lazy { findViewById<TextView>(R.id.tvForgotPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        url = getString(R.string.API_URL) + "iniciarSesion.php"

        setupActionBar()
        setupClickListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = "Iniciar Sesión en SolidarApp"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back)
        }
    }

    private fun setupClickListeners() {
        btLogin.setOnClickListener {
            val userEmail = etEmail.text.toString()
            val userPassword = etPassword.text.toString()
            iniciarSesion(userEmail, userPassword)
        }

        tvForgotPassword.setOnClickListener {
            startResetPasswordActivity()
        }
    }

    private fun iniciarSesion(userEmail: String, userPassword: String) {
        val stringRequest = createStringRequest(userEmail, userPassword)

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun createStringRequest(userEmail: String, userPassword: String): StringRequest {
        return object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                handleResponse(response, userEmail)
            },
            Response.ErrorListener { error ->
                showToast("Error: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("userEmail" to userEmail, "userPassword" to userPassword)
            }
        }
    }

    private fun handleResponse(response: String, userEmail: String) {
        try {
            val jsonResponse = JSONObject(response)
            val success = jsonResponse.getBoolean("success")
            val message = jsonResponse.getString("message")

            if (success) {
                showToast(message)
                saveUserEmail(userEmail)
                startFeedActivity()
            } else {
                showToast(message)
            }
        } catch (e: Exception) {
            showToast("Error al procesar la Respuesta del Servidor: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserEmail(userEmail: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("userEmail", userEmail).apply()
    }

    private fun startFeedActivity() {
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
    }

    private fun startResetPasswordActivity() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }
}
