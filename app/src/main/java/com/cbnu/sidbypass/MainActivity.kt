package com.cbnu.sidbypass

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MainActivity : AppCompatActivity() {
    var id = 0
    var timerSeconds = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        val idText = findViewById<EditText>(R.id.idText)

        idText.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                parseId(idText)
            }
        })
        saveButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                parseId(idText)
            }
        })

        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        id = sharedPref.getString("id", "0").toString().toInt()

        if (id > 0) {
            val idText = findViewById<EditText>(R.id.idText)

            idText.setText(id.toString())
            LaunchQR()
        }
        else {
            Toast.makeText(this, "최초 실행 시 학번을 입력해주세요.", Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch {
            val refreshText = findViewById<TextView>(R.id.refreshText)

            while(true) {
                delay(1000)
                if (id == 0) continue
                timerSeconds--

                if (timerSeconds == 0) {
                    LaunchQR()
                    timerSeconds = 30
                }
                refreshText.setText("${timerSeconds}초 후 새로고침...")
            }
        }
    }

    fun getSID(): String {
        var date: LocalDateTime = LocalDateTime.now().plusSeconds(4)
        val month = date.monthValue.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val hour = date.hour.toString().padStart(2, '0')
        val min = date.minute.toString().padStart(2, '0')
        val second = date.second.toString().padStart(2, '0')
        val ms = (date.nano / 10000).toString().padStart(5, '0')
        val sid = "CB   00${id}${date.year}${month}${day}${hour}${min}${second}${ms}"

        //val refresh = findViewById<TextView>(R.id.refreshText)
        //refresh.text = sid
        //Toast.makeText(this, sid, Toast.LENGTH_LONG).show()

        return sid
    }

    fun LaunchQR() {
        val sid = getSID()
        val bitmap = generateBitmapQRCode(sid)
        val imageView = findViewById<ImageView>(R.id.imageView)

        imageView.setImageBitmap(bitmap)
    }

    private fun generateBitmapQRCode(contents: String): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(contents, BarcodeFormat.QR_CODE, 1024, 1024)
    }

    fun parseId(idText: EditText) {
        val idParsed = idText.text.toString().toIntOrNull()
        if (idParsed != null) id = idParsed
        else id = 0

        if (id > 0) {
            val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
            val edit = sharedPref.edit()

            edit.putString("id", id.toString())
            edit.apply()
            timerSeconds = 30

            LaunchQR()
        }
        else {
            Toast.makeText(this@MainActivity, "학번 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show()
        }
    }
}