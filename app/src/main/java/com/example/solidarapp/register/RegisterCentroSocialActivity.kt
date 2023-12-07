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

class RegisterCentroSocialActivity : AppCompatActivity() {

    private lateinit var url: String

    private lateinit var ivSocialCenterImage : ImageView
    private lateinit var etSocialCenterUser : EditText
    private lateinit var etSocialCenterEmail : EditText
    private lateinit var etSocialCenterPassword : EditText
    private lateinit var etSocialCenterConfirmPassword : EditText
    private lateinit var etSocialCenterDir : EditText
    private lateinit var etSocialCenterCol : EditText
    private lateinit var etSocialCenterCity : EditText
    private lateinit var etSocialCenterDescription : EditText
    private lateinit var btSocialCenterRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_centrosocial_layout)

        supportActionBar?.setTitle("Registro de Centro Social")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        ivSocialCenterImage = findViewById(R.id.ivSocialCenterImage)

        etSocialCenterUser = findViewById(R.id.etSocialCenterUser)
        etSocialCenterEmail = findViewById(R.id.etSocialCenterEmail)

        etSocialCenterPassword = findViewById(R.id.etSocialCenterPassword)
        etSocialCenterConfirmPassword = findViewById(R.id.etSocialCenterConfirmPassword)

        etSocialCenterDir = findViewById(R.id.etSocialCenterDir)
        etSocialCenterCol = findViewById(R.id.etSocialCenterCol)
        etSocialCenterCity = findViewById(R.id.etSocialCenterCity)
        etSocialCenterDescription = findViewById(R.id.etSocialCenterDescription)

        btSocialCenterRegister = findViewById(R.id.btSocialCenterRegister)

        btSocialCenterRegister.setOnClickListener{
            val socialCenterName = etSocialCenterUser.text.toString()
            val socialCenterEmail = etSocialCenterEmail.text.toString()
            val socialCenterPassword = etSocialCenterPassword.text.toString()
            val socialCenterConfirmPassword = etSocialCenterConfirmPassword.text.toString()
            val socialCenterStreet = etSocialCenterDir.text.toString()
            val socialCenterSuburb = etSocialCenterCol.text.toString()
            val socialCenterCity = etSocialCenterCity.text.toString()
            val socialCenterDescription = etSocialCenterDescription.text.toString()

            registrarCentroSocial(socialCenterName, socialCenterEmail, socialCenterPassword, socialCenterConfirmPassword, socialCenterStreet, socialCenterSuburb, socialCenterCity, socialCenterDescription)
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

    private fun registrarCentroSocial(socialCenterName: String, socialCenterEmail: String, socialCenterPassword: String, socialCenterConfirmPassword: String,  socialCenterStreet: String, socialCenterSuburb: String, socialCenterCity: String, socialCenterDescription: String){
        url = getString(R.string.API_URL)+"registrarCentroSocial.php"

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                // Procesar la respuesta JSON...
                try {
                    val jsonResponse = JSONObject(response)

                    // Verificar si el registro fue Exitoso.
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
                    // Manejar cualquier error al analizar la respuesta JSON
                    showToast("Error al Procesar la Respuesta del Servidor: " + e.toString())
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
                params["socialCenterName"] = socialCenterName
                params["socialCenterEmail"] = socialCenterEmail
                params["socialCenterPassword"] = socialCenterPassword
                params["socialCenterConfirmPassword"] = socialCenterConfirmPassword
                params["socialCenterStreet"] = socialCenterStreet
                params["socialCenterSuburb"] = socialCenterSuburb
                params["socialCenterCity"] = socialCenterCity
                params["socialCenterDescription"] = socialCenterDescription
                return params
            }
        }

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


}