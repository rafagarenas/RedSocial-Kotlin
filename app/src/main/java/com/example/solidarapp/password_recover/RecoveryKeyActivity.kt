package com.example.solidarapp.password_recover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText

import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.solidarapp.R
import org.json.JSONObject

class RecoveryKeyActivity : AppCompatActivity() {

    private lateinit var url: String
    private lateinit var etRecoveryKey: EditText
    private lateinit var btSendRecoveryPasswordKey: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recovery_key_layout)

        supportActionBar?.setTitle("Recuperar Contraseña")

        val userRecoveryEmail = intent.getStringExtra("userRecoveryEmail").toString()

        etRecoveryKey = findViewById(R.id.etRecoveryKey)
        btSendRecoveryPasswordKey = findViewById(R.id.btSendRecoveryPasswordKey)

        btSendRecoveryPasswordKey.setOnClickListener {
            val recoveryKey = etRecoveryKey.text.toString()
            validarClaveRecuperacion(userRecoveryEmail, recoveryKey)
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

    private fun validarClaveRecuperacion(userRecoveryEmail: String, recoveryKey: String) {
        url = getString(R.string.API_URL)+"validarClaveRecuperacion.php"

        val stringRequest = createStringRequest(userRecoveryEmail, recoveryKey, url)

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun createStringRequest(
        userRecoveryEmail: String,
        recoveryKey: String,
        url: String
    ): StringRequest {
        return object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                handleResponse(response, userRecoveryEmail)
            },
            Response.ErrorListener { error ->
                handleError(error)
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "userRecoveryEmail" to userRecoveryEmail,
                    "recoveryKey" to recoveryKey
                )
            }
        }
    }

    private fun handleResponse(response: String, userRecoveryEmail: String) {
        try {
            val jsonResponse = JSONObject(response)

            val success = jsonResponse.getBoolean("success")
            val message = jsonResponse.getString("message")

            if (success) {
                showToast(message)

                val intent = Intent(this, ChangePasswordActivity::class.java)
                intent.putExtra("userRecoveryEmail", userRecoveryEmail)
                startActivity(intent)
            } else {
                showToast(message)
            }
        } catch (e: Exception) {
            showToast("Error al procesar la Respuesta del Servidor: " + e.toString())
            e.printStackTrace()
        }
    }

    private fun handleError(error: VolleyError) {
        println("Error: ${error.message}")
        showToast("${error.message}")
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}