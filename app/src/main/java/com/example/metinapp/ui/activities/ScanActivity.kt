package com.example.metinapp.ui.activities

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible

import com.example.metinapp.R
import com.example.metinapp.utils.ContextExtensions
import com.example.metinapp.utils.ScanActivityUtils
import com.example.metinapp.views.TextArea
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class ScanActivity : AppCompatActivity() {
    lateinit var englishTurkishTranslator:Translator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // 0 = OKUMA MODU
        // 1 = DÜZENLEME MODU
        var durum = 0

        val eklentiler = ContextExtensions(this)

        //butonlar
        val secilenVeyaCekilenFoto: ImageView = findViewById(R.id.secilenVeyaCekilenFoto)
        val sonucMetin: TextView = findViewById(R.id.sonucMetin)
        val sonucuKapat: ImageView = findViewById(R.id.translateKapat)
        val sonucuPaylas: ImageView = findViewById(R.id.translatePaylas)
        val sonucuDuzenle: FloatingActionButton = findViewById(R.id.sonucuDuzenle)
        val sonucuCevir: FloatingActionButton = findViewById(R.id.sonucuCevir)
        val sonucuCevirTr: FloatingActionButton = findViewById(R.id.sonucuCevirTrEn)
        val sonucTextDuzenleyici: TextArea = findViewById(R.id.sonucTextDuzenleyici)
        val sonucTextContainer: ScrollView = findViewById(R.id.sonucTextContainer)

        secilenVeyaCekilenFoto.setImageBitmap(ScanActivityUtils.bitmap)
        sonucMetin.text = ScanActivityUtils.sonucText




        sonucuCevir.setOnClickListener{
            ScanActivityUtils.ceviriText = ""
            cevirmeIslemi()
            val intent = Intent(applicationContext, TranslateActivity::class.java)
                startActivity(intent)

        }
        sonucuCevirTr.setOnClickListener{
            ScanActivityUtils.ceviriText = ""
            cevirmeIslemiTr()
            val intent = Intent(applicationContext, TranslateActivity::class.java)
            startActivity(intent)

        }


        sonucuKapat.setOnClickListener {
            finish()
        }

        sonucuPaylas.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, ScanActivityUtils.sonucText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        sonucuDuzenle.setOnClickListener {
            if (!sonucTextContainer.isVisible) {
                sonucuDuzenle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done))

                sonucMetin.visibility = View.INVISIBLE
                sonucTextContainer.visibility = View.VISIBLE

                sonucTextDuzenleyici.setText(ScanActivityUtils.sonucText, TextView.BufferType.EDITABLE)

                durum = 1
            } else {
                sonucuDuzenle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))

                ScanActivityUtils.sonucText = sonucTextDuzenleyici.text.toString()
                sonucMetin.text = ScanActivityUtils.sonucText

                sonucTextContainer.visibility = View.INVISIBLE
                sonucMetin.visibility = View.VISIBLE

                if (durum == 1) {
                    eklentiler.hideKeyboard(sonucuDuzenle)
                }

                durum = 0
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        ScanActivityUtils.run {
            sonucText = ""
            bitmap = createBitmap(1, 1)
        }
    }

    private fun cevirmeIslemi(){

        val options:TranslatorOptions=TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.TURKISH)
            .build()

        englishTurkishTranslator=Translation.getClient(options)




        //dil dosyası yoksa indirme işlemi
        englishTurkishTranslator.downloadModelIfNeeded().addOnSuccessListener {

            metniCevir()


        }.addOnFailureListener {

            ScanActivityUtils.ceviriText = "HATA ${it.message}"
        }

    }
    private fun cevirmeIslemiTr(){

        val options:TranslatorOptions=TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        englishTurkishTranslator=Translation.getClient(options)




        //dil dosyası yoksa indirme işlemi
        englishTurkishTranslator.downloadModelIfNeeded().addOnSuccessListener {

            metniCevir()


        }.addOnFailureListener {

            ScanActivityUtils.ceviriText = "HATA ${it.message}"
        }

    }


    private fun metniCevir() {

        englishTurkishTranslator.translate(ScanActivityUtils.sonucText).addOnSuccessListener {

            ScanActivityUtils.ceviriText =it
        }
            .addOnFailureListener {

                ScanActivityUtils.ceviriText = "HATA ${it.message}"
            }

    }



}