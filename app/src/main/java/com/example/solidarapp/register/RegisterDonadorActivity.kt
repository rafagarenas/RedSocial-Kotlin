package com.example.solidarapp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.widget.Toast
import com.example.solidarapp.InitialActivity
import com.example.solidarapp.R

class RegisterDonadorActivity : AppCompatActivity() {

    private lateinit var url: String

    private lateinit var ivAltruistImage : ImageView
    private lateinit var etAltruistUser : EditText
    private lateinit var etAltruistEmail : EditText
    private lateinit var etAltruistPassword : EditText
    private lateinit var etAltruistConfirmPassword : EditText
    private lateinit var btAltruistRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_donador_layout)

        supportActionBar?.setTitle("Registro de Donador Altruista")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        ivAltruistImage = findViewById(R.id.ivAltruistImage)
        ivAltruistImage.setImageResource(R.drawable.profile)

        etAltruistUser = findViewById(R.id.etNewUserName)
        etAltruistEmail = findViewById(R.id.etNewEmail)
        etAltruistPassword = findViewById(R.id.etAltruistPassword)
        etAltruistConfirmPassword = findViewById(R.id.etAltruistConfirmPassword)

        btAltruistRegister = findViewById(R.id.btSaveChanges)

        btAltruistRegister.setOnClickListener{
            val userName = etAltruistUser.text.toString()
            val userEmail = etAltruistEmail.text.toString()
            val userPassword = etAltruistPassword.text.toString()
            val userConfirmPassword = etAltruistConfirmPassword.text.toString()

            registrarDonadorAltruista(userName, userEmail, userPassword, userConfirmPassword)
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

    private fun registrarDonadorAltruista(userName: String, userEmail: String, userPassword: String, userConfirmPassword: String){
        url = getString(R.string.API_URL)+"registrarDonadorAltruista.php"

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                // Procesar la respuesta JSON...
                try {
                    val jsonResponse = JSONObject(response)

                    // Verificar si el registro fue Exitoso
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    if (success) {
                        showToast("$message")

                        val intent = Intent(this, InitialActivity::class.java)
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
                // Manejar errores de la solicitud aquí

                println("Error: ${error.message}")
                showToast("${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userName"] = userName
                params["userEmail"] = userEmail
                params["userPassword"] = userPassword
                params["userConfirmPassword"] = userConfirmPassword
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    }

}