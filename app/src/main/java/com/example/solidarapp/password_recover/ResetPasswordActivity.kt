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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.solidarapp.R
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var url: String

    private lateinit var etRecoveryEmail: EditText
    private lateinit var btRecoveryPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_layout)

        supportActionBar?.setTitle("Recuperar Contraseña")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        etRecoveryEmail = findViewById(R.id.etRecoveryEmail)
        btRecoveryPassword = findViewById(R.id.btRecoveryPassword)


        btRecoveryPassword.setOnClickListener {
            val userRecoveryEmail = etRecoveryEmail.text.toString()
            enviarClaveRecuperacion(userRecoveryEmail)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun enviarClaveRecuperacion(userRecoveryEmail: String){
        url = getString(R.string.API_URL)+"recuperarContraseña.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast("$message")

                        val intent = Intent(this, RecoveryKeyActivity::class.java)
                        intent.putExtra("userRecoveryEmail", userRecoveryEmail)
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
                params["userRecoveryEmail"] = userRecoveryEmail
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

}