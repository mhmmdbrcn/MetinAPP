package com.example.metinapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.metinapp.R
import com.example.metinapp.utils.ContextExtensions
import com.example.metinapp.utils.ScanActivityUtils
import com.example.metinapp.views.TextArea
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TranslateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)


        // 0 = OKUMA MODU
        // 1 = DÜZENLEME MODU
        var durum = 0

        val eklentiler = ContextExtensions(this)

        //butonlar

        val sonucTranslateMetin: TextView = findViewById(R.id.sonucTranslateMetin)
        val sonucTranslateContainer : ScrollView = findViewById(R.id.sonucTranslateContainer)
        val sonucTranslateDuzenleyici: TextArea = findViewById(R.id.sonucTranslateDuzenleyici)

        val sonucMetin: TextView = findViewById(R.id.sonucMetin)
        val translateKapat: ImageView = findViewById(R.id.translateKapat)
        val translatePaylas: ImageView = findViewById(R.id.translatePaylas)
        val sonucuDuzenle: FloatingActionButton = findViewById(R.id.sonucuDuzenle)



        sonucMetin.text = ScanActivityUtils.sonucText
        sonucTranslateMetin.text = ScanActivityUtils.ceviriText

        translateKapat.setOnClickListener {
            val intent = Intent(applicationContext,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

        translatePaylas.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, ScanActivityUtils.ceviriText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        sonucuDuzenle.setOnClickListener {
            if (!sonucTranslateContainer.isVisible) {
                sonucuDuzenle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done))

                sonucTranslateMetin.visibility = View.INVISIBLE
                sonucTranslateContainer.visibility = View.VISIBLE

                sonucTranslateDuzenleyici.setText(ScanActivityUtils.ceviriText, TextView.BufferType.EDITABLE)

                durum = 1
            } else {
                sonucuDuzenle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))

                ScanActivityUtils.ceviriText = sonucTranslateDuzenleyici.text.toString()
                sonucTranslateMetin.text = ScanActivityUtils.ceviriText

                sonucTranslateContainer.visibility = View.INVISIBLE
                sonucTranslateMetin.visibility = View.VISIBLE

                if (durum == 1) {
                    eklentiler.hideKeyboard(sonucuDuzenle)
                }

                durum = 0
            }
        }
    }




}