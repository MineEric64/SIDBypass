package com.cbnu.sidbypass

import android.graphics.Bitmap
import android.os.Bundle
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
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MainActivity : AppCompatActivity() {
    var id = 0

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

        //TODO: 설정 만들기
        if (id > 0) {
            LaunchQR()
        }
        else {
            Toast.makeText(this, "최초 실행 시 학번을 입력해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    fun getSID(): String {
        val date: LocalDateTime = LocalDateTime.now().plusHours(9).plusSeconds(11)

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
            LaunchQR()
        }
        else {
            Toast.makeText(this@MainActivity, "학번 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show()
        }
    }
}