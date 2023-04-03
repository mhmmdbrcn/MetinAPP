package com.example.metinapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.metinapp.R


class SplashActivity : AppCompatActivity() {

    companion object {

        const val animasyonZamani: Long = 5000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(this.mainLooper).postDelayed({


            startActivity(Intent(this, MainActivity::class.java))


            finish()

        }, animasyonZamani)

    }

}