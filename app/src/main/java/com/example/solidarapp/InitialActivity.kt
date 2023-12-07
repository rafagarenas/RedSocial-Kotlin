package com.example.solidarapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.solidarapp.register.RegisterCentroSocialActivity
import com.example.solidarapp.register.RegisterDonadorActivity

class InitialActivity : AppCompatActivity() {
    private lateinit var btLogin1: Button
    private lateinit var btSoyAltruista: Button
    private lateinit var btSoyCentroSocial: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial_layout)

        setupActionBar()
        initializeViews()
        setupClickListeners()
    }

    private fun setupActionBar() {
        supportActionBar?.title = "Bienvenido a SolidarApp"
    }

    private fun initializeViews() {
        btLogin1 = findViewById(R.id.btLogin1)
        btSoyAltruista = findViewById(R.id.btSoyAltruista)
        btSoyCentroSocial = findViewById(R.id.btSoyCentroSocial)
    }

    private fun setupClickListeners() {
        btLogin1.setOnClickListener { startLoginActivity() }
        btSoyAltruista.setOnClickListener { startRegisterDonadorActivity() }
        btSoyCentroSocial.setOnClickListener { startRegisterCentroSocialActivity() }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startRegisterDonadorActivity() {
        val intent = Intent(this, RegisterDonadorActivity::class.java)
        startActivity(intent)
    }

    private fun startRegisterCentroSocialActivity() {
        val intent = Intent(this, RegisterCentroSocialActivity::class.java)
        startActivity(intent)
    }
}
