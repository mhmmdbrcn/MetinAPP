package com.example.metinapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.FileProvider
import com.example.metinapp.R
import com.example.metinapp.utils.ScanActivityUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

private lateinit var cekilenFoto: File

class MainActivity : AppCompatActivity() {

   //sınıf ismiyle erişilebilen nesneler
    companion object {
        private const val IMAGE_CHOOSE = 1000
        private const val TAKE_PHOTO = 1001
        private const val PERMISSION_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        //buton tanımları
        val altMenu: BottomNavigationView = findViewById(R.id.altMenu)
        val altMenuContainer: MotionLayout = findViewById(R.id.altMenuContainer)
        val hafizadanFotoSec: FloatingActionButton = findViewById(R.id.hafizadanFotoSec)
        val fotoCek: FloatingActionButton = findViewById(R.id.fotoCek)



        altMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> println("KAYNAK")
                else -> println("Nothing")
            }
            true
        }


       //fotoğraf çekme butonu işlevi
        fotoCek.setOnClickListener {
            if (altMenuContainer.currentState == altMenuContainer.endState) {
                altMenuContainer.transitionToStart()
            }

            //içerik alışveriş nesnesi (intent)
            val cekilenFotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cekilenFoto = getFotoDosyası()
            val providerFile = FileProvider.getUriForFile(
                this,
                "com.example.metinapp.fileprovider",
                cekilenFoto
            )
            cekilenFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

            try {
                startActivityForResult(cekilenFotoIntent, TAKE_PHOTO)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Camera could not open", Toast.LENGTH_SHORT).show()
            }
        }


        //galeriden foto seçme işlevi
        hafizadanFotoSec.setOnClickListener {
            if (altMenuContainer.currentState == altMenuContainer.endState) {
                altMenuContainer.transitionToStart()
            }

            //hafıza okuma izin kontrolü
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                galeridenSec()
            }
        }
    }

    //galeriden seçme fonksiyonu
    private fun galeridenSec() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    //izin kontrolü sonucuna göre yapılacaklar
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galeridenSec()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //fotoyu geçici olarak hafızaya alma
    private fun getFotoDosyası(): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("photo", ".jpg", directoryStorage)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val takenPhoto = BitmapFactory.decodeFile(cekilenFoto.absolutePath)
GlobalScope.launch {
                extractedText(takenPhoto)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val chosenPhoto = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
            GlobalScope.launch {
                extractedText(chosenPhoto)
            }
        }
    }

    // Metin Recognize işlemi
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun extractedText(bitmap: Bitmap) {
        val fotoMetinRecognizer: TextRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        ScanActivityUtils.bitmap = bitmap

        inputImage.let { image ->
            val value = GlobalScope.async {
                fotoMetinRecognizer.process(image).addOnSuccessListener {
                    ScanActivityUtils.sonucText = it.text
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Metin Bulunamadı", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            value.await().addOnSuccessListener {
                val intent = Intent(applicationContext, ScanActivity::class.java)
                startActivity(intent)
            }
        }
    }
}